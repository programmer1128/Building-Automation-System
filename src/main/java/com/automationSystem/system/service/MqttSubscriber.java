package com.automationSystem.system.service;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.automationSystem.system.DataTransferObjects.DashboardStatsDto;
import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.repository.DeviceRepository;
import com.automationSystem.system.repository.MicrocontrollerRepository;
import com.automationSystem.system.websocket.TelemetryWebSocketHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MqttSubscriber 
{

     private final TelemetryWebSocketHandler webSocketHandler;
     private final DashboardAggregatorService aggregatorService;
     private final MicrocontrollerRepository mcRepository;
    
     // 🔥 NEW DEPENDENCIES: We need these to route and parse the specific device
     private final DeviceRepository deviceRepository;
     private final SensorRoutingService routingService;
    private final ObjectMapper objectMapper;

    public MqttSubscriber(TelemetryWebSocketHandler webSocketHandler, 
                          DashboardAggregatorService aggregatorService,
                          MicrocontrollerRepository mcRepository,
                          DeviceRepository deviceRepository,
                          SensorRoutingService routingService,
                          ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.aggregatorService = aggregatorService;
        this.mcRepository = mcRepository;
        this.deviceRepository = deviceRepository;
        this.routingService = routingService;
        this.objectMapper = objectMapper;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomingMqttMessage(Message<String> message) {
        String payload = message.getPayload();
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String macAddress = topic.split("/")[2]; 

        // 1. Find who owns this ESP32
        mcRepository.findByMacAddress(macAddress).ifPresent(mc -> {
            String ownerUsername = mc.getOwner().getUsername();

            try {
                // 2. Peek into the JSON to find WHICH device on this ESP32 sent the reading
                JsonNode jsonNode = objectMapper.readTree(payload);
                String deviceName = jsonNode.get("deviceId").asText(); // e.g., "Living Room AC"

                // 3. Fetch the actual Device from the database so we know its Type (AC, SMOKE, etc.)
                Device device = deviceRepository.findByDeviceNameAndController(deviceName, mc)
                        .orElseThrow(() -> new IllegalArgumentException("Device not found!"));

                // 4. Pass it to the Universal Pipeline!
                DeviceStateDto newState = routingService.routeAndProcess(device.getDeviceType(), payload, mc, device);

                // 5. Fetch the user's complete state from Redis
                DashboardStatsDto dashboard = aggregatorService.getStaticStats(ownerUsername);

                // 6. Update the map: This overwrites the old reading for this specific device
                // We use the database ID as the key so devices with the same name don't overwrite each other
                dashboard.getActiveDevices().put(device.getDeviceId().toString(), newState);

                // 7. Aggregate Math: Loop up all values to calculate new Total Power
                long newTotalPower = 0;
                 for (DeviceStateDto state : dashboard.getActiveDevices().values()) 
                 {
                    newTotalPower += state.getNumericValue();
                 }
                 dashboard.setTotalPower(newTotalPower);
                // 8. Save updated state back to Redis so it's ready for the next reading
                aggregatorService.saveStateToRedis(ownerUsername, dashboard);

                // 9. Convert the ENTIRE dashboard map to JSON and send to React!
                // React will receive {"totalPower": 1650, "activeDevices": {"1": {...}, "2": {...}}}
                String fullJsonToReact = objectMapper.writeValueAsString(dashboard);
                webSocketHandler.sendToUser(ownerUsername, fullJsonToReact);

            } catch (Exception e) {
                System.err.println("Failed to process telemetry pipeline: " + e.getMessage());
            }
        });
    }
}