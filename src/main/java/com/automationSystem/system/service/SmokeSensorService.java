package com.automationSystem.system.service;

import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
import com.automationSystem.system.pipeline.SensorProcessor;


@Service
public class SmokeSensorService implements SensorProcessor 
{
     @Override
     public boolean supports(String type) 
     {
         return type.equalsIgnoreCase("SMOKE");
     }

     @Override
     public DeviceStateDto processPayload(String rawMqttPayload, Microcontroller mc, Device device) 
     {
         DeviceStateDto state = new DeviceStateDto();
         state.setMacAddress(mc.getMacAddress());
         state.setName(device.getDeviceName());
         state.setType("SMOKE");
         // ... extract smoke data ...
         state.setNumericValue(0); // Doesn't add to power total
         state.setReading("Clear"); // or "DETECTED!"
         state.setStatus("ON");
         return state;
     }
}