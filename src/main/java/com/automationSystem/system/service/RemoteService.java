package com.automationSystem.system.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.automationSystem.system.DataTransferObjects.GetBleDevicesRequest;
import com.automationSystem.system.DataTransferObjects.LearnProtocolRequest;
import com.automationSystem.system.DataTransferObjects.SendCommandRequest;
import com.automationSystem.system.entity.Remote;
import com.automationSystem.system.entity.User;
import com.automationSystem.system.entity.VirtualDevice;
import com.automationSystem.system.entity.VirtualDeviceCommands;
import com.automationSystem.system.repository.RemoteRepository;
import com.automationSystem.system.repository.UserRepository;
import com.automationSystem.system.repository.VirtualDeviceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;



@Service
public class RemoteService 
{
     @Autowired
     private MqttGateway mqttGateway;

     @Autowired
     private ObjectMapper objectMapper;


     @Autowired
     VirtualDeviceRepository virtualDeviceRepository;


     @Autowired
     UserRepository userRepository;


     @Autowired
     RemoteRepository remoteRepository;
     
     
     @Value("${youtube.api.key}")
     private String apiKey;

     public void checkProtocol(LearnProtocolRequest request)
     {
         //The request contains the device name and the macAddress of the remote it is 
         //connected to.Now we send a request to this remote to accept the signal from
         //the remote to identify if it is raw or identified.
         System.out.println("Device name = "+request.getDeviceName());
         //String deviceName=request.getDeviceName();
         if(request.getRemoteMacAddress()==null)
         {
             System.out.println("Null mac address");
             return;
         }
         //creating a new virtual device

         //send message to the remote(esp32)
         Map<String,Object> payLoad = new HashMap<>();
         payLoad.put("op","DISCOVER");
         payLoad.put("timeout",30);

         try
         {
             String jsonPayLoad = objectMapper.writeValueAsString(payLoad);
             String topic="commands/ir/"+request.getRemoteMacAddress();
             mqttGateway.sendToMqtt(topic,jsonPayLoad);
         }
         catch(Exception e)
         {
             throw new RuntimeException(e.getMessage());
         }
         
     }


     public ResponseEntity<?> processIRResult(Message<?> message)
     {
         //this gets the body of the message
         String payLoad= (String)message.getPayload(); 

         //now we got the json as entire string
         String [] jsonArray = payLoad.split(",");

         //0 is protocol, 1 is macaddress 2 is raw protocol check in boolean
         //3 is hex code 4 is bits
         
         //if the protocol is raw then we have to turn on manual learning mode
         //for the remote to learn commands else we can use the commands from 
         //the ir library itself
         System.out.println(payLoad);

         String protocol=jsonArray[0].split(":")[1];


         protocol=protocol.substring(1,protocol.length()-1);

         boolean isRaw=false;
         if(jsonArray[2].split(":")[1].equals("TRUE"))
         {
             isRaw=true;
         }

         VirtualDevice virtualDevice = virtualDeviceRepository.findByIsNew("YES")
             .orElseThrow(()-> new RuntimeException("No such devices with name "));

        
         virtualDevice.setRaw(isRaw);
         
         
         if(isRaw)
         {
             return ResponseEntity.ok("Protocol is Raw please enter learning mode");
         }
         return ResponseEntity.ok("Protocol is identified as = "+protocol);
     }

     //method used to send command to remote so that remote sends signal to device
     public ResponseEntity<?> sendCommand(SendCommandRequest request)
     {
         VirtualDevice device= virtualDeviceRepository.findByDeviceName(request.getDeviceName())
             .orElseThrow(()-> new RuntimeException("no such devivces found"));


         VirtualDeviceCommands command=device.getCommands().stream()
             .filter(c->c.getButtonName().toUpperCase().contains((request.getCommand().toUpperCase())))
             .findFirst().orElseThrow(()-> new RuntimeException("button not learned yet"));
 
         //now we construct the json that we will send to the esp32 for sending command 
         //to the device
         ObjectNode mqttPayLoad=objectMapper.createObjectNode();

         //System.out.println("hello");
         mqttPayLoad.put("op","SEND");

         mqttPayLoad.put("p",command.getProtocol());

         mqttPayLoad.put("hex",command.getHexCode());

         mqttPayLoad.put("bits",command.getBits());


         String topic="commands/ir/"+device.getRemote().getMacAddress()+"/send";
         System.out.println(topic);
         mqttGateway.sendToMqtt(topic,mqttPayLoad.toString());


         return ResponseEntity.ok("Signal sent successfully");
     }


     public List<String> getBluetoothDevices(GetBleDevicesRequest request)
     {
         List<String> devices = new ArrayList<>();
         
         String userName=request.getUserName();

         String remoteName=request.getRemoteName();

         User user = userRepository.findByUsername(userName).orElseThrow(()->
             new RuntimeException("no such users "));

         //find the list of remotes assigned to user
         List<Remote> remotes=remoteRepository.findByOwner(user);
         Remote requestedRemote; boolean found=false;

         //now we check if the requested remote is in the list of remotes
         //allotted to user and get that remote. we will send the mqtt command
         //to that remote using the macaddress
         for(Remote remote:remotes)
         {
             if(remote.getRemoteName().equals(remoteName))
             {
                 requestedRemote=remote; found=true;
                 break;
             }
         }

         if(!found)
         {
             System.out.println("no such remote for this user");
         }

         
         return devices;
     }

     //method to play songs using the youtubemusic api
     public String searchSong(String songName) throws Exception
     {
         YouTube youTubeService= new YouTube.Builder(
             new NetHttpTransport(),
             new JacksonFactory(),
             null).setApplicationName("BuildingAutomationSystem").build();


         YouTube.Search.List search = youTubeService.search().list(Collections.singletonList("id,snippet"));

         search.setKey(apiKey);
         search.setQ(songName);
         search.setType(Collections.singletonList("video"));
         search.setMaxResults(1L);

         SearchListResponse response = search.execute();

         return getStreamingUrl(new ObjectMapper().valueToTree(response.getItems().get(0)));
     }

     //method to get the streaming url
     public String getStreamingUrl(JsonNode node) throws IOException 
     {
          // The videoId is located at: node -> "id" -> "videoId"
         JsonNode idNode = node.get("id");
    
         if (idNode == null || !idNode.has("videoId")) 
         {
             throw new IOException("videoId not found in the YouTube API response. Check the Search JSON structure.");
         }

         String videoId = idNode.get("videoId").asText();
         System.out.println("Extracted Video ID: " + videoId); // Useful for debugging

         ProcessBuilder pb = new ProcessBuilder(
             "yt-dlp", "-g", "-f", "bestaudio", "https://www.youtube.com/watch?v=" + videoId);

         Process process = pb.start();
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

         String directUrl = reader.readLine();
         return directUrl;
     }
}
