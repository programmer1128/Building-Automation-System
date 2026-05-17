package com.automationSystem.system.pipeline;

import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;

public interface SensorProcessor 
{
     // Tells the pipeline if this service handles this specific device type
     boolean supports(String type); 
    
     // Processes the raw MQTT string and returns the universal DTO
     DeviceStateDto processPayload(String rawMqttPayload, Microcontroller mc, Device device);
}
