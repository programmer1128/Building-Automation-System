 package com.automationSystem.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CommandTemplate 
{
     @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     Long id;

     private String brandName;    // e.g., Samsung
     private String protocol;     // e.g., SAMSUNG (must match ESP32 library strings)
     private String buttonName;   // e.g., POWER
     private String hexCode;      // e.g., 0xE0E040BF
     private int bits;            // e.g., 32
    
     private String source;       // 'IRDB' or 'TASMOTA' (helps you track data quality)
}
