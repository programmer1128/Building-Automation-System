package com.automationSystem.system.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Entity
@Data
public class VirtualDevice 
{
      @Id
      @GeneratedValue(strategy=GenerationType.IDENTITY) 
      private Long id;   

      private String deviceName;

      private boolean isRaw;

      private String isNew;

      @ManyToOne
      @JoinColumn(name="remote_id")
      private Remote remote;

      @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "virtual_device_command_map",
        joinColumns = @JoinColumn(name = "virtual_device_id"),
        inverseJoinColumns = @JoinColumn(name = "command_id")
    )
      private List<VirtualDeviceCommands> commands;    
}
