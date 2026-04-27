package com.automationSystem.system.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

//this is the subscriber class for the mqtt. This recieves messages from the esp32

@Component
public class MqttSubscriber 
{
     
     @Autowired
     RemoteService remoteService;

     @ServiceActivator(inputChannel = "mqttInputChannel")
     public void handleMessage(Message<?> message) 
     {
         try
         {
             remoteService.processIRResult(message);
         }
         catch(Exception e )
         {
             System.out.println(e.getMessage());
         }
          
     }
}