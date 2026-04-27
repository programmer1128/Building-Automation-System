package com.automationSystem.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan; // <--- IMPORT THIS

@SpringBootApplication
@IntegrationComponentScan(basePackages = "com.automationSystem.system")
public class BuildingAutomationSystemApplication 
{

 	 public static void main(String[] args) 
	 {
	     SpringApplication.run(BuildingAutomationSystemApplication.class, args);
	 }

}
