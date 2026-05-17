package com.automationSystem.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.automationSystem.system.DataTransferObjects.DashboardStatsDto;
import com.automationSystem.system.DataTransferObjects.DeviceRegisterRequest;
import com.automationSystem.system.DataTransferObjects.LoginRequest;
import com.automationSystem.system.DataTransferObjects.MicrocontrollerRegisterRequest;
import com.automationSystem.system.DataTransferObjects.PinChangeRequest;
import com.automationSystem.system.DataTransferObjects.RegisterRemoteDeviceRequest;
import com.automationSystem.system.DataTransferObjects.RegisterRemoteRequest;
import com.automationSystem.system.DataTransferObjects.RegisterUserRequest;
import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
import com.automationSystem.system.entity.Remote;
import com.automationSystem.system.entity.User;
import com.automationSystem.system.entity.VirtualDevice;
import com.automationSystem.system.entity.VirtualDeviceCommands;
import com.automationSystem.system.repository.DeviceRepository;
import com.automationSystem.system.repository.MicrocontrollerRepository;
import com.automationSystem.system.repository.RemoteRepository;
import com.automationSystem.system.repository.UserRepository;
import com.automationSystem.system.repository.VirtualDeviceCommandsRepository;
import com.automationSystem.system.repository.VirtualDeviceRepository;

@Service
public class AdminService 
{
       @Autowired
       private MicrocontrollerRepository microcontrollerRepository;

       @Autowired
       private DeviceRepository deviceRepository;

       @Autowired
       private UserRepository userRepository;

       @Autowired
       private RemoteRepository remoteRepository;

       @Autowired
       private PasswordEncoder encoder;


       @Autowired
       private VirtualDeviceRepository virtualDeviceRepository;

       @Autowired
       private VirtualDeviceCommandsRepository commandsRepository;

       @Autowired
       private com.automationSystem.system.config.JwtService jwtService;

       
       @Autowired
       private DashboardAggregatorService aggregatorService;

       //method to register a new user
       @Transactional
       public ResponseEntity<?> registerUser(RegisterUserRequest request)
       {
             if(userRepository.existsByUsername(request.getUsername()))
             {
                   System.out.println("User already exists");

                   return ResponseEntity.status(HttpStatus.CONFLICT)
                         .body("Username already exists");
             }
             //creating a new object user
             User user = new User();

             //setting the user details
             user.setUsername(request.getUsername());
             user.setPassword(encoder.encode(request.getPassword()));
             user.setMicrocontrollers(new ArrayList<>());
             user.setRemotes(new ArrayList<>());
             
             userRepository.save(user);
             return ResponseEntity.status(HttpStatus.CREATED).body("User is created");
       }

       //method to login the user
       public ResponseEntity<?> loginUser(LoginRequest request)
       {
             User user = userRepository.findByUsername(request.getUsername())
                   .orElseThrow(()-> new RuntimeException("no such user found"));

             if(encoder.matches(request.getPassword(), user.getPassword()))
             {
                   String token = jwtService.generateToken(user.getUsername());
                   java.util.Map<String, String> response = new java.util.HashMap<>();
                   response.put("token", token);
                   response.put("username", user.getUsername());
                   return ResponseEntity.ok().body(response);
             }
             return ResponseEntity.badRequest().body("Wrong password");
       }
   

       public User getUserDetails(String username)
       {
             return userRepository.findByUsername(username)
                   .orElseThrow(()-> new RuntimeException("User not found"));
       }

       //method to register a new esp32 board
       @Transactional
       public Microcontroller registerMicrocontroller(MicrocontrollerRegisterRequest request)
       {
             String macAddress=request.getMacAddress();
             Optional<Microcontroller> existingBoard=microcontrollerRepository.findByMacAddress(macAddress);

             if(existingBoard.isPresent())
             {
                   throw new RuntimeException("Board with MAC Address "+macAddress+" Already there");
             }

             //if microcontroller is new then we register it in our database
             Microcontroller microcontroller = new Microcontroller();

             microcontroller.setMacAddress(macAddress);
             microcontroller.setIpAddress(request.getIpAddress());
             microcontroller.setLocation(request.getLocation());
             
             //now we set the microcontroller to its user
             User user = userRepository.findByUsername(request.getUsername())
                   .orElseThrow(()-> new RuntimeException("User not found"));
             microcontroller.setOwner(user);
             user.getMicrocontrollers().add(microcontroller);
             //save the microcontroller in the database
             return microcontrollerRepository.save(microcontroller);
       }

       //method to register a new Device
       @Transactional
       public Device registerDevice(DeviceRegisterRequest request)
       {
             //now we have to check if the microcontroller this device is supposed to 
             //be registering on exists or not
             Microcontroller microcontroller=microcontrollerRepository.findByMacAddress(request.getParentMacAddress()).
                   orElseThrow(()-> new RuntimeException("Device with mac address not found"));

             //creating a new Device
             Device device= new Device();

             device.setDeviceName(request.getName());
             device.setDeviceType(request.getType());
             device.setDeviceStatus("OFF");
             device.setPinNumber(request.getPinNumber());
 
             //setting the microcontroller to which this device is connected and registered
             device.setController(microcontroller);
             User user=microcontroller.getOwner();
             //increment the counter in the user table for the devices
             //serRepository.incrementDeviceCount();
             userRepository.incrementDeviceCount(user.getUsername());

            

             try 
             {
                   // Fetch the user's current live state from Redis
                   DashboardStatsDto dashboard = aggregatorService.getStaticStats(user.getUsername());
            
                   // Increment the total monitored count
                   dashboard.setTotalMonitored(dashboard.getTotalMonitored() + 1);
            
                   // Overwrite the old Redis cache with the new count
                   aggregatorService.saveStateToRedis(user.getUsername(), dashboard);
            
             } 
             catch (Exception e) 
             {
                   System.out.println("Warning: Could not update Redis cache during registration: " + e.getMessage());
             }
             return deviceRepository.save(device);
       }

       //method to change the pin number of the device
       public void changePin(PinChangeRequest request)
       {
             Device device = deviceRepository.findByDeviceName(request.getDeviceName())
                         .orElseThrow(()->new RuntimeException("No such devices found"));
             
             //now we set the pin number of the current device to the requested pin number
             //this is very helpful if any pin is broken and we need to place the device 
             //on another pin
             device.setPinNumber(request.getPinNumber());

             deviceRepository.save(device);
             
       }

       //method to register a new remote for the user
       @Transactional
       public ResponseEntity<?> registerRemote(RegisterRemoteRequest request)
       {
             Optional<Remote> existingRemote = remoteRepository.findByMacAddress(request.getMacaddress());
             if(existingRemote.isPresent())
             {
                   throw new RuntimeException("Remote is already registered");
             }

             //if remote is not already there we create a new remote object
             Remote remote = new Remote();

             remote.setMacAddress(request.getMacaddress());
             User user= userRepository.findByUsername(request.getUsername())
                   .orElseThrow(()-> new RuntimeException("User is not there"));  
             remote.setOwner(user);
             remote.setRemoteName(request.getRemoteName());
             remote.setVirtualDevices(new ArrayList<>());

             //saving the remote in the database
             remoteRepository.save(remote);
             return ResponseEntity.status(HttpStatus.CREATED).body("Remote registered");
       }


       //method to register a new remote device
       @Transactional
       public ResponseEntity<?> registerRemoteDevice(RegisterRemoteDeviceRequest request)
       {
             Optional<VirtualDevice> existingDevice=virtualDeviceRepository
                   .findByDeviceName(request.getDeviceName());
             if(existingDevice.isPresent())
             {
                   throw new RuntimeException("Device already registered with this name");
             }

             //If new device then we make the new registration
             VirtualDevice device = new VirtualDevice();

             device.setDeviceName(request.getDeviceName());
             Remote remote=remoteRepository.findByMacAddress(request.getRemoteMacAddress()).
                   orElseThrow(()-> new RuntimeException("No such remote exists"));

             device.setRemote(remote);
             
             
             List<VirtualDeviceCommands> commands=commandsRepository
                   .findByProtocol(request.getDeviceBrand().toUpperCase());

             device.setCommands(new ArrayList<>(commands));
             //saving the device in the database
             virtualDeviceRepository.save(device);
             return ResponseEntity.status(HttpStatus.CREATED).body("Remote Device Registered");
       }
}
