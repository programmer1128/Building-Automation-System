package com.automationSystem.system.DataTransferObjects;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeviceStateDto implements Serializable 
{
     private String macAddress; // Acts as your Serial No
     private String name;       // "Living Room AC"
     private String type;       // "CURRENT", "SMOKE", "TEMP"
     private String reading;    // "1450 W" or "Clear" or "24 °C" (Stored as string for UI)
     private double numericValue; // 1450.0 (Used secretly for math/summing)
     private boolean anomaly;   
     private String status;     // "ON" or "OFF"
}