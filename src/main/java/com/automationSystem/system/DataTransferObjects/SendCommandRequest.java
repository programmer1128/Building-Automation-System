package com.automationSystem.system.DataTransferObjects;

import lombok.Data;

@Data
public class SendCommandRequest 
{
     private String macAddress;
     private String deviceName;
     private String command;
}
