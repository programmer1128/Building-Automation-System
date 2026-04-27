package com.automationSystem.system.controller;

 

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.automationSystem.system.DataTransferObjects.GetBleDevicesRequest;
import com.automationSystem.system.DataTransferObjects.LearnProtocolRequest;
import com.automationSystem.system.DataTransferObjects.SendCommandRequest;
import com.automationSystem.system.service.RemoteService;


@RestController
@RequestMapping("/api")
public class RemoteController 
{
     @Autowired
     RemoteService remoteService;

     @PostMapping("/learn/protocol")
     public ResponseEntity<?> learnProtocol(@RequestBody LearnProtocolRequest request)
     {
         try
         {
             remoteService.checkProtocol(request);
             return  ResponseEntity.ok(""); 
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }  
     }


     @PostMapping("/command")
     public ResponseEntity<?> sendIrCommand(@RequestBody SendCommandRequest request)
     {
         try
         {
             return remoteService.sendCommand(request);
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
          
     }

     @GetMapping("/bluetoothdevices")
     public ResponseEntity<?> getBluetoothDevices(@RequestBody GetBleDevicesRequest request)
     {
         try
         {
             List<String> devices=remoteService.getBluetoothDevices(request);

             return ResponseEntity.ok("");
         }
         catch(Exception e)
         {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }


     @CrossOrigin(origins = "*") // Allow React Native to connect
     @GetMapping("/music/search")
     public ResponseEntity<String> search(@RequestParam String songName) 
     {
         try 
         {
             String url = remoteService.searchSong(songName);
             return ResponseEntity.ok(url);
         } 
         catch (Exception e) 
         {
             return ResponseEntity.status(500).body("Error: " + e.getMessage());
         }
    }
}
