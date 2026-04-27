package com.automationSystem.system.DataTransferObjects;

//This is the data transfer object for the json of the switch on request from the user for a device
import lombok.Data;

@Data
public class DeviceSwitchRequest 
{
     private String deviceName;
     private String deviceType;
     private String status;
}
