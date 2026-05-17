package com.automationSystem.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

      private String deviceName;   
      private String deviceType;   
      private String deviceStatus; 
    
      private int pinNumber; 

      @ManyToOne
      @JoinColumn(name = "microcontroller_id")
      @JsonIgnore
      private Microcontroller controller; 
}
