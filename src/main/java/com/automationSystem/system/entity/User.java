package com.automationSystem.system.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
@Entity
@Data
public class User 
{
     @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     private Long Id;

     @Column(name="username")
     private String username;

     @Column(nullable=false)
     private String password;

     @Column(name = "device_count")
     private long deviceCount;

     @Column(name = "anomaly_count")
     private long anomalyCount;

     @OneToMany(mappedBy = "owner",cascade=CascadeType.ALL)
     private List<Microcontroller> microcontrollers;
     
     @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
     private List<Remote> remotes;

}
