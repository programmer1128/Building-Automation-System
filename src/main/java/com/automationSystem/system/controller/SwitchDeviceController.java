package com.automationSystem.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automationSystem.system.DataTransferObjects.DeviceSwitchRequest;
import com.automationSystem.system.service.SwitchService;


@RestController
@RequestMapping("/api")
public class SwitchDeviceController 
{
     @Autowired
     SwitchService service;

     @PostMapping("/switch")
     public ResponseEntity<?> switchOn(@RequestBody DeviceSwitchRequest request)
     {
         //we pass on this request to the switch service and based on the user 
         //request the device will be switched on for this api end point
         try
         {
             return service.switchDevice(request);
         }
         catch(Exception e)
         {
              return ResponseEntity.badRequest().body(e.getMessage());
         }
        
     }
}
