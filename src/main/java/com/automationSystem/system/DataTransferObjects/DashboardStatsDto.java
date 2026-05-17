package com.automationSystem.system.DataTransferObjects;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class DashboardStatsDto implements Serializable 
{
     private static final long serialVersionUID = 1L;
    
     private long totalMonitored;
     private long activeAnomalies;
     private long totalPower;
     private Map<String, DeviceStateDto> activeDevices = new ConcurrentHashMap<>();

     public DashboardStatsDto(long totalMonitored, long activeAnomalies) 
     {
         this.totalMonitored = totalMonitored;
         this.activeAnomalies = activeAnomalies;
     }

     // Getters and Setters
     public long getTotalMonitored() 
     { 
         return totalMonitored; 
     }
     public void setTotalMonitored(long totalMonitored) 
     { 
         this.totalMonitored = totalMonitored; 
     }
    
     public long getActiveAnomalies() 
     { 
         return activeAnomalies; 
     }
     public void setActiveAnomalies(long activeAnomalies) 
     { 
         this.activeAnomalies = activeAnomalies; 
     }
}