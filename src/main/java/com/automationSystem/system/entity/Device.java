package com.automationSystem.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="devices")
public class Device 
{
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long deviceId;

      private String deviceName;   // "Room 1 Light"
      private String deviceType;   // "LIGHT"
      private String deviceStatus; // "OFF"
    
      private int pinNumber; // The GPIO pin on the ESP32 as the devices will all be 
      //connected to esp32 via pins and for controlling the devices we need to know
      //which pin it is connected to.

      //
      @ManyToOne
      @JoinColumn(name = "microcontroller_id")
      private Microcontroller controller; 
}
