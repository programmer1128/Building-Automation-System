package com.automationSystem.system.service;

import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
import com.automationSystem.system.pipeline.SensorProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimpleDeviceProcessor implements SensorProcessor 
{

     private final ObjectMapper objectMapper;

     public SimpleDeviceProcessor(ObjectMapper objectMapper) 
     {
         this.objectMapper = objectMapper;
     }

     @Override
     public boolean supports(String type) 
     {
         return type.equalsIgnoreCase("LIGHT") || 
               type.equalsIgnoreCase("FAN") || 
               type.equalsIgnoreCase("ACTUATOR");
     }

     @Override
     public DeviceStateDto processPayload(String rawMqttPayload, Microcontroller mc, Device device) 
     {
         DeviceStateDto state = new DeviceStateDto();
         state.setMacAddress(mc.getMacAddress());
         state.setName(device.getDeviceName());
         state.setType(device.getDeviceType()); // Keeps the explicit type (e.g., LIGHT)

         // 1. Simple appliances don't send live power numbers in their own packets
         state.setNumericValue(0); 

         try 
         {
             JsonNode jsonNode = objectMapper.readTree(rawMqttPayload);
            
             // 2. Extract the current switch state from the ESP32 relay
             String status = jsonNode.has("deviceStatus") ? jsonNode.get("deviceStatus").asText() : "OFF";
             state.setStatus(status);
            
             // 3. Render the reading column cleanly based on state
             if (status.equalsIgnoreCase("ON")) 
             {
                 state.setReading("Active");
             }  
             else 
             {
                 state.setReading("Idle / Powered Down");
             }

             // 4. Extract basic anomaly flags if the relay fails to trip
             boolean anomaly = jsonNode.has("anomaly") && jsonNode.get("anomaly").asBoolean();
             state.setAnomaly(anomaly);

         } 
         catch (Exception e) 
         {
             state.setStatus("OFF");
             state.setReading("Offline");
             state.setAnomaly(true);
         }

         return state;
     }
}