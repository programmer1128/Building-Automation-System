package com.automationSystem.system.service;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.automationSystem.system.entity.CommandTemplate;
import com.automationSystem.system.entity.VirtualDeviceCommands;
import com.automationSystem.system.repository.CommandTemplateRepository;
import com.automationSystem.system.repository.VirtualDeviceCommandsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class ParserService 
{
     @Autowired
     ObjectMapper objectMapper;
     

     @Autowired
     VirtualDeviceCommandsRepository commandsRepository;
     
     public void ParseTasmotaHtml(File htmlFile)
     {
         try 
         {
             Document doc = Jsoup.parse(htmlFile, "UTF-8");
             Elements tables = doc.select("table"); // Select every table in the doc

             for (Element table : tables) 
             {
                 // Find the header text immediately BEFORE this specific table
                 Element brandHeader = table.previousElementSiblings().select("h1, h2, h3, b").first();
                 String brandName = (brandHeader != null) ? brandHeader.text().split(" ")[0] : "Unknown";

                 //System.out.println(brandName);
                 Elements rows = table.select("tr"); // Get rows for THIS table
                 for (Element row : rows) 
                 {
                     Elements cells = row.select("td");
                     if (cells.size() == 2) 
                     {
                         String buttonName = cells.get(0).text().trim();
                         String rawJson = cells.get(1).text().trim();

                         // Skip the header row
                         if (buttonName.equalsIgnoreCase("button")) continue;
                    
                    
                         try 
                         {
                             JsonNode node = objectMapper.readTree(rawJson);
                             VirtualDeviceCommands commands = new VirtualDeviceCommands();
                             //CommandTemplate template = new CommandTemplate();
                        
                             // Use the specific brand we just found!
                             commands.setBrandName(brandName); 
                             //template.setBrandName(brandName); 
                             //template.setButtonName(buttonName);
                             commands.setButtonName(buttonName);
                             //template.setProtocol(node.get("Protocol").asText());
                             commands.setProtocol(node.get("Protocol").asText());
                             //template.setHexCode(node.get("Data").asText());
                             commands.setHexCode(node.get("Data").asText());
                             //template.setBits(node.get("Bits").asInt());
                             commands.setBits(node.get("Bits").asInt());
                             //template.setSource("TASMOTA");

                             commands.setSource("TASMOTA");

                             commands.setRawData("");
                             commands.setRaw(false);

                             commandsRepository.save(commands);
                         } 
                         catch (Exception e) 
                         {
                             // Log errors for invalid JSON cells
                             System.out.println(e.getMessage());
                         } 
                     }
                 }
             }
         } 
         catch (IOException e) 
         {
             e.printStackTrace();
         }
     }

     public void parseIrdbFolder(String folderPath)
     {
         
     }
}
