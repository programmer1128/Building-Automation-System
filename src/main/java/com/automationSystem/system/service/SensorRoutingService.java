package com.automationSystem.system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.DeviceStateDto;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
import com.automationSystem.system.pipeline.SensorProcessor;

@Service
public class SensorRoutingService 
{
     
     // Spring automatically finds CurrentSensorService, SmokeSensorService, etc., and puts them here!
     private final List<SensorProcessor> processors;
 
     public SensorRoutingService(List<SensorProcessor> processors) 
     {
         this.processors = processors;
     }

     public DeviceStateDto routeAndProcess(String deviceType, String payload, Microcontroller mc, Device device) 
     {
         for (SensorProcessor processor : processors) 
         {
             if (processor.supports(deviceType)) 
             {
                 return processor.processPayload(payload, mc, device);
             }
         }
         throw new IllegalArgumentException("No sensor service found for type: " + deviceType);
    }
}