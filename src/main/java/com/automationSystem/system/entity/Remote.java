package com.automationSystem.system.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
@Entity
@Data
public class Remote 
{
     @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     private Long Id;

     @Column(unique=true)
     private String macAddress;

     @Column(name="name")
     private String remoteName;

     @ManyToOne
     @JoinColumn(name="user_Id")
     private User owner;

     @OneToMany(mappedBy = "remote", cascade=CascadeType.ALL)
     private List<VirtualDevice> virtualDevices;

}
