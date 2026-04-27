package com.automationSystem.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="sensors")
public class Sensor 
{
     @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     private Long id;

     @Column(name="sensor_type")
     private String sensorType; // "TEMP", "GAS"

     @Column(name="status")
     private String status; // "ACTIVE", "ERROR"

     //This is the link between the microcontroller and the sensors as to which 
     //sensor is connected to which microcontroller
     @ManyToOne
     @JoinColumn(name = "microcontroller_id")
     private Microcontroller controller;

     //This link serves the purpose as to which device is watcged by which sensor;
     @OneToOne
     @JoinColumn(name = "monitored_device_id")
     private Device monitoredDevice;
}