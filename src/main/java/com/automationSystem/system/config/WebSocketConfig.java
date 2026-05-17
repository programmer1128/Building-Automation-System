package com.automationSystem.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.automationSystem.system.websocket.TelemetryWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer 
{
 
     private final TelemetryWebSocketHandler telemetryWebSocketHandler;

     public WebSocketConfig(TelemetryWebSocketHandler telemetryWebSocketHandler) 
     {
         this.telemetryWebSocketHandler = telemetryWebSocketHandler;
     }

     @Override
     public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) 
     {
         // Exposes the exact endpoint we wrote in React
         registry.addHandler(telemetryWebSocketHandler, "/ws/telemetry")
                .setAllowedOrigins("http://localhost:3000", "http://192.168.0.162:3000");
     }
}