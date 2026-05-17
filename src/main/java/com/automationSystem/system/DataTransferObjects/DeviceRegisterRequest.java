package com.automationSystem.system.DataTransferObjects;

import lombok.Data;

@Data
public class DeviceRegisterRequest 
{
      //name of the registered device
      private String name;       
      //device type - light,fan,heater etc
      private String type;     
      //which pin of the esp32 is this device connected to
      private Integer pinNumber;     
      //which esp32 is this device connected to
      private String parentMacAddress;   
}
