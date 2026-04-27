package com.automationSystem.system.DataTransferObjects;

import lombok.Data;

@Data
public class RegisterRemoteRequest 
{
     private String username;
     private String macaddress;    
     private String remoteName; 
}
