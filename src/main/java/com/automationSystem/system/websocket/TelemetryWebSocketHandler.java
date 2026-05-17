package com.automationSystem.system.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.automationSystem.system.service.DashboardAggregatorService;

@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler 
{

     // 1. Change to a Map so we can link a Username (String) to their Session
     private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
     private final DashboardAggregatorService aggregatorService;
 
     public TelemetryWebSocketHandler(DashboardAggregatorService aggregatorService) 
     {
         this.aggregatorService = aggregatorService;
     }

     @Override
     public void afterConnectionEstablished(WebSocketSession session) throws Exception 
     {
         // Extract the username from the URL (e.g., ?username=aritra)
         String query = session.getUri().getQuery();
         String username = query.split("=")[1]; 
        
         // Save the username into the session attributes so we can find it later
         session.getAttributes().put("username", username);
        
         // Add them to our active routing map
         userSessions.put(username, session);
         System.out.println("✅ WebSocket Connected for user: " + username);

         // 🔥 Fetch THEIR specific Redis data and send it immediately
         String initialPayload = aggregatorService.generateLiveDashboardPayload(username, "{}");
         session.sendMessage(new TextMessage(initialPayload));
     }

     @Override
     public void afterConnectionClosed(WebSocketSession session, CloseStatus status) 
     {
         String username = (String) session.getAttributes().get("username");
         if (username != null) 
         {
             userSessions.remove(username);
             System.out.println("❌ WebSocket Disconnected for user: " + username);
         }
     }

     // 2. NEW METHOD: Send data ONLY to a specific user, not everyone!
     public void sendToUser(String username, String jsonPayload) 
     {
         WebSocketSession session = userSessions.get(username);
         if (session != null && session.isOpen()) 
         {
             try 
             {
                 session.sendMessage(new TextMessage(jsonPayload));
             } 
             catch (IOException e) 
             {
                 System.err.println("Failed to send to user: " + username);
             }
         }
     }
}