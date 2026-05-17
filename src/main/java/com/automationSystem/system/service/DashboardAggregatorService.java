package com.automationSystem.system.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.DashboardStatsDto;
import com.automationSystem.system.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DashboardAggregatorService 
{

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // Spring provides this automatically for JSON parsing

     public DashboardAggregatorService(UserRepository userRepository, ObjectMapper objectMapper) 
     {
         this.userRepository = userRepository;
         this.objectMapper = objectMapper;
     }

     // This is the Redis method we wrote earlier!
     @Cacheable(value = "dashboardStats", key = "#username")
     public DashboardStatsDto getStaticStats(String username) 
     {
         System.out.println("⚠️ Cache Miss! Hitting MySQL Database for user: " + username);
         long totalDevices = userRepository.getDeviceCountByUsername(username);
         // Assuming you have an anomaly counter or just hardcode 0 for now
         long anomalies = 0; 
         return new DashboardStatsDto(totalDevices, anomalies);
     }

     // 🔥 Your newly fixed method
     public String generateLiveDashboardPayload(String username, String liveMqttData) 
     {
         try 
         {
            // 1. Fetch real counts from Redis Cache (or MySQL if cache miss)
            DashboardStatsDto stats = getStaticStats(username);
            long totalMonitored = stats.getTotalMonitored();
            long activeAnomalies = stats.getActiveAnomalies();

            // 2. Extract the live power reading from the ESP32's MQTT packet
            long currentPower = 0;
            if (liveMqttData != null && !liveMqttData.equals("{}")) {
                JsonNode mqttNode = objectMapper.readTree(liveMqttData);
                if (mqttNode.has("power")) {
                    currentPower = mqttNode.get("power").asLong();
                }
            }

            // 3. Safely construct the JSON string with all variables matching the %d placeholders!
            return String.format(
                "{\"totalPower\": %d, \"totalMonitored\": %d, \"activeAnomalies\": %d, \"liveUpdate\": %s}",
                currentPower, totalMonitored, activeAnomalies, liveMqttData
            );

        } catch (Exception e) 
        {
            e.printStackTrace();
            return "{}"; // Safe fallback so the WebSocket doesn't crash
        }
    }

     @CachePut(value = "dashboardStats", key = "#username")
     public DashboardStatsDto saveStateToRedis(String username, DashboardStatsDto updatedDashboard) 
     {
         return updatedDashboard;
     }
}