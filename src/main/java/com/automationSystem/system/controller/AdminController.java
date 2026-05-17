package com.automationSystem.system.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automationSystem.system.DataTransferObjects.DeviceRegisterRequest;
import com.automationSystem.system.DataTransferObjects.LoginRequest;
import com.automationSystem.system.DataTransferObjects.MicrocontrollerRegisterRequest;
import com.automationSystem.system.DataTransferObjects.RegisterRemoteDeviceRequest;
import com.automationSystem.system.DataTransferObjects.RegisterRemoteRequest;
import com.automationSystem.system.DataTransferObjects.RegisterUserRequest;
import com.automationSystem.system.service.AdminService;
import com.automationSystem.system.service.ParserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController 
{
     @Autowired
     AdminService adminService;

     @Autowired
     ParserService parserService;

     //method to register a new user
     
     @PostMapping("/register/user")
     public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request)
     {
         try
         {
             return adminService.registerUser(request);
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }

    
     @PostMapping("/signin/user")
     public ResponseEntity<?> loginUser(@RequestBody LoginRequest request)
     {
         try
         {
             return adminService.loginUser(request);
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }

     @PostMapping("/register/microcontroller")   
     public ResponseEntity<?> registerMicrocontroller(@RequestBody MicrocontrollerRegisterRequest request)
     {
         try 
         {
             return ResponseEntity.ok(adminService.registerMicrocontroller(request));   
         } 
         catch (Exception e) 
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }


     @PostMapping("/register/device")
     public ResponseEntity<?> registerDevice(@RequestBody DeviceRegisterRequest request)
     {
         try
         {
             return ResponseEntity.ok(adminService.registerDevice(request));       
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }

     @PostMapping("/register/remote")
     public ResponseEntity<?> registerRemoteDevice(@RequestBody RegisterRemoteRequest request)
     {
         try
         {
             return adminService.registerRemote(request);
         }
         catch(Exception e)
         { 
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }


     @PostMapping("/register/remoteDevice")
     public ResponseEntity<?> registerRemoteDevices(@RequestBody RegisterRemoteDeviceRequest request)
     {
          try
          {
             return adminService.registerRemoteDevice(request);
          }
          catch(Exception e)
          {
             return ResponseEntity.badRequest().body(e.getMessage());
          }
     }

     @GetMapping("/user/{username}")
     public ResponseEntity<?> getUserDetails(@PathVariable String username)
     {
         try
         {
             return ResponseEntity.ok(adminService.getUserDetails(username));
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }

     @PostMapping("/fillcommandstasmota")
     public ResponseEntity<?> fillCommands()
     {
         try
         {
             File tasmotaFile = new File("src/main/java/com/automationSystem/system/dataFiles/Codes for IR Remotes - Tasmota.html");
             parserService.ParseTasmotaHtml(tasmotaFile);
             return ResponseEntity.ok("");
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
}
