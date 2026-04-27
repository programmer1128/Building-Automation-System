package com.automationSystem.system.DataTransferObjects;

import lombok.Data;

@Data
public class RegisterRemoteDeviceRequest 
{
     private String deviceName;
     private String deviceType;
     private String deviceBrand;
     private String remoteMacAddress;
}
