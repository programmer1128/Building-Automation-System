package com.automationSystem.system.DataTransferObjects;

import lombok.Data;
@Data
public class PinChangeRequest 
{
     private String deviceName;
     private int pinNumber;        
}
