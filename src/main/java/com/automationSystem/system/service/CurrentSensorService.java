package com.automationSystem.system.service;

import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
import com.automationSystem.system.pipeline.SensorProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CurrentSensorService implements SensorProcessor 
{

    private final ObjectMapper objectMapper;

    // Inject ObjectMapper so we can parse the incoming MQTT JSON string
    public CurrentSensorService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String type) {
        return type.equalsIgnoreCase("CURRENT") || type.equalsIgnoreCase("AC");
    }

    @Override
    public DeviceStateDto processPayload(String rawMqttPayload, Microcontroller mc, Device device) {
        DeviceStateDto state = new DeviceStateDto();
        state.setMacAddress(mc.getMacAddress());
        state.setName(device.getDeviceName());
        state.setType(device.getDeviceType());
        
        try {
            JsonNode jsonNode = objectMapper.readTree(rawMqttPayload);
            
            // 🔥 THE FIX: Actually extract the dynamic power from your Bash script!
            double power = jsonNode.has("power") ? jsonNode.get("power").asDouble() : 0.0;
            
            state.setNumericValue(power);
            state.setReading(power + " W");
            
            // Also grab the dynamic status and anomaly flags
            String status = jsonNode.has("deviceStatus") ? jsonNode.get("deviceStatus").asText() : "ON";
            state.setStatus(status);
            
            boolean anomaly = jsonNode.has("anomaly") && jsonNode.get("anomaly").asBoolean();
            state.setAnomaly(anomaly);
            
        } catch (Exception e) {
            System.err.println("Failed to parse Current Sensor JSON: " + e.getMessage());
            state.setNumericValue(0);
            state.setReading("Data Error");
            state.setStatus("OFF");
        }
        
        return state;
    }
}