package com.automationSystem.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "microcontrollers")
public class Microcontroller 
{
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @Column(unique = true) // MAC address should be unique
      private String macAddress; 
     
      private String location;   
      private String ipAddress;

      @ManyToOne
      private User owner;
}