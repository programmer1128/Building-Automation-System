package com.automationSystem.system.service;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

//This is the Mqtt publisher that is used to send messages from the java springboot to 
//the esp32

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway 
{
     // Send a message with a specific topic
     void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

     // Send a message to the default topic defined in configuration
     void sendToMqtt(String payload);
}

