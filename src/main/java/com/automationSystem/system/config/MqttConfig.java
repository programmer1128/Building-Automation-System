package com.automationSystem.system.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator; // Import this
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class MqttConfig {

     @Bean
     public MqttConnectOptions mqttConnectOptions() 
     {
         MqttConnectOptions options = new MqttConnectOptions();
         options.setServerURIs(new String[]{"tcp://localhost:1883"});
         options.setCleanSession(true);
         options.setAutomaticReconnect(true);
         options.setUserName("esp_admin");
         options.setPassword("Chiko2005#".toCharArray());
         return options;
     }

     @Bean
     public MqttPahoClientFactory mqttClientFactory() 
     {
         DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
         factory.setConnectionOptions(mqttConnectOptions());
         return factory;
     }

     @Bean
     public MessageChannel mqttOutboundChannel() 
     {
         return new DirectChannel();
     }

     @Bean
     @ServiceActivator(inputChannel = "mqttOutboundChannel")
     public MessageHandler mqttOutboundMessageHandler() 
     {
         MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("springBootSender", mqttClientFactory());
         messageHandler.setAsync(true);
         messageHandler.setDefaultTopic("test");
         return messageHandler;
     }

     @Bean 
     public MessageChannel mqttInputChannel()
     {
         return new DirectChannel();
     }

     @Bean
     public MessageProducer inbound() 
     {
         // The topic uses '+' as a wildcard for the MAC address
         MqttPahoMessageDrivenChannelAdapter adapter =
             new MqttPahoMessageDrivenChannelAdapter(
                 "springBootReceiver", 
                 mqttClientFactory(), 
                 "status/ir/+/result"
             );
    
         adapter.setCompletionTimeout(5000);
         adapter.setConverter(new DefaultPahoMessageConverter());
         adapter.setQos(1);
    
         // This connects the MQTT flow to the channel Subscriber is watching
         adapter.setOutputChannel(mqttInputChannel()); 
    
         return adapter;
     }
    
    
}
