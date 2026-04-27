package com.automationSystem.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.*;

@Entity
@Data
@Table(name = "virtual_device_commands")
public class VirtualDeviceCommands 
{
      @Id
      @GeneratedValue(strategy=GenerationType.IDENTITY)
      private Long Id;    

      String brandName;

      //we need a label that will tell us what command it is
      private String buttonName;


      //protocol on which the device works
      private String protocol;

      //hex code field is to send commands to the device. Devices with remotes
      //work on these hexcode
      private String hexCode;


      private Integer bits;

      //now is the case when the protocol is not known by the esp32 library
      @Column(columnDefinition="TEXT")
      private String rawData;

      private String source;

      private boolean isRaw;

      @ManyToMany(mappedBy="commands")
      private List<VirtualDevice> virtualDevices = new ArrayList<>();

}
