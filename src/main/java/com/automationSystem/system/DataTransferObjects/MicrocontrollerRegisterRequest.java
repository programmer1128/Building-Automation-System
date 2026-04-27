package com.automationSystem.system.DataTransferObjects;

import lombok.Data;

@Data
public class MicrocontrollerRegisterRequest 
{
      private String macAddress; //unique to every esp32 
      private String location; //location of where the esp32 is placed
      private String ipAddress; //ip address of esp32  
      private String username; 
}
