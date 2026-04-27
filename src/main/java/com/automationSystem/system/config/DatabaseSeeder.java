package com.automationSystem.system.config; // Update to your actual package path

import com.automationSystem.system.DataTransferObjects.*;
import com.automationSystem.system.service.AdminService; // Update to your actual import
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    private AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        
        // ⚠️ SAFETY SWITCH: Change to 'true' to run the seeder, then immediately back to 'false'
        boolean runSeeder = false; 

        if (!runSeeder) {
            log.info("Database Seeder is disabled. Set 'runSeeder = true' to populate DB.");
            return;
        }

        log.info("Starting massive database seeding (5,000 Users, 300,000 total entities)...");
        long startTime = System.currentTimeMillis();

        int totalUsers = 5000;
        int itemsPerUser = 20;

        for (int i = 1; i <= totalUsers; i++) {
            String currentUsername = "jmeter_user_" + i;

            try {
                // 1. Register User
                RegisterUserRequest userReq = new RegisterUserRequest();
                userReq.setUsername(currentUsername);
                userReq.setPassword("password123"); 
                adminService.registerUser(userReq);

                // 2. Register 20 Microcontrollers, Devices, and Remotes linked to this user
                for (int j = 1; j <= itemsPerUser; j++) {
                    
                    // Generate unique identifiers for relational mapping
                    String macAddress = "MAC_ESP32_" + i + "_" + j;
                    String deviceName = "SmartBulb_" + i + "_" + j;
                    String remoteName = "RemoteControl_" + i + "_" + j;

                    // --- Register Microcontroller ---
                    MicrocontrollerRegisterRequest mcReq = new MicrocontrollerRegisterRequest();
                    mcReq.setMacAddress(macAddress);
                    mcReq.setLocation("Zone_" + j);
                    mcReq.setIpAddress("192.168." + (i % 255) + "." + j);
                    mcReq.setUsername(currentUsername);
                    adminService.registerMicrocontroller(mcReq);

                    // --- Register Device ---
                    DeviceRegisterRequest devReq = new DeviceRegisterRequest();
                    devReq.setName(deviceName);
                    devReq.setType("Light");
                    devReq.setPinNumber(j); 
                    devReq.setParentMacAddress(macAddress); // Links to the MC we just made
                    adminService.registerDevice(devReq);

                    // --- Register Remote ---
                    RegisterRemoteRequest remReq = new RegisterRemoteRequest();
                    remReq.setUsername(currentUsername);
                    remReq.setMacaddress(macAddress); // Links to the MC we just made
                    remReq.setRemoteName(remoteName);
                    adminService.registerRemote(remReq);
                }
            } catch (Exception e) {
                log.error("Failed to seed data for user: " + currentUsername + " - " + e.getMessage());
            }

            // Print progress every 100 users so you know it hasn't frozen
            if (i % 100 == 0) {
                log.info("Successfully seeded " + i + " users and their hardware infrastructure...");
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("Database Seeding Complete! Inserted 305,000 records in " + (endTime - startTime) / 1000 + " seconds.");
    }
}