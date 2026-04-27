package com.automationSystem.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service; 

import com.automationSystem.system.DataTransferObjects.DeviceSwitchRequest;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.repository.DeviceRepository;
//this is the switch service for switching a device on/off
@Service
public class SwitchService 
{
     @Autowired
     private DeviceRepository deviceRepository;

     @Autowired
     @Qualifier("mqttOutboundChannel") 

     private MessageChannel mqttChannel;

     public ResponseEntity<?> switchDevice(DeviceSwitchRequest request)
     {
         String deviceName=request.getDeviceName();   
         Device device = deviceRepository.findByDeviceName(deviceName)
                 .orElseThrow(()-> new RuntimeException("Device not found with deviced name"+deviceName));

         //now that we have the device we can have 4 conditions for the state
         String status=request.getStatus(); 

         //the status of the device in correspondence to the user request can have 4 conditions
         // device- on , request - on
         // device- off , request -on
         //device - off, request -on
         //device -on , request off
         //for the first 2 conditions we do not have to perform any operations
         if(status.equals(device.getDeviceStatus()))
         {
             if(status.equals("OFF"))
             {
                 return ResponseEntity.ok(deviceName+" Device already OFF");
             }
             else
             {
                 return ResponseEntity.ok(deviceName+" Device already ON");
             }
         }

         //now based on the request we will send the esp32 on or off status along with the pin
         //number of the device so that the esp32 can perform the proper operation
         String macAddress=device.getController().getMacAddress();
         int pinNumber=device.getPinNumber();

         String topic = "home/devices/" + macAddress + "/command";
         //we will now send a message to the esp32 to turn on the device\
         String payload = String.format("{\"pin\": %d, \"status\": \"%s\"}", pinNumber, status);
      
         // We build a message with the payload and the Topic header
         Message<String> message = MessageBuilder
                 .withPayload(payload)
                 .setHeader(MqttHeaders.TOPIC, topic)
                 .build();

         mqttChannel.send(message);

         device.setDeviceStatus(status);
         deviceRepository.save(device);

         return ResponseEntity.ok("Command sent: " + status);
     }
}
