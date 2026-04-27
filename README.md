# Building Automation System

A robust, Spring Boot-based Building Automation System designed to control and monitor home appliances and sensors via ESP32 microcontrollers using the MQTT protocol. The system supports device switching, IR remote virtualization, sensor monitoring, and YouTube music streaming integration.

## 🚀 Features

- **Device Management:** Control physical devices (Lights, Fans, etc.) connected to ESP32 GPIO pins.
- **IR Remote Virtualization:** Learn and replicate IR signals to control devices with remotes (TVs, ACs, etc.).
- **Sensor Monitoring:** Real-time monitoring of Temperature, Gas, Smoke, and Motion sensors.
- **MQTT Integration:** Seamless bi-directional communication with hardware.
- **YouTube Integration:** Search and stream audio from YouTube using `yt-dlp`.
- **User Management:** Secure owner-based access to microcontrollers and remotes.

---

## 🏗 Database Schema Design

The system uses a relational database to manage complex relationships between users, hardware, and virtual commands.

### Entity Relationship Overview:
- **User:** The owner of the system. Has many Microcontrollers and Remotes.
- **Microcontroller:** Identified by MAC Address. Acts as a hub for physical Devices and Sensors.
- **Device:** Represents a physical load (e.g., a Bulb) connected to a specific GPIO `pinNumber`.
- **Sensor:** Monitors environmental data and can be linked to a specific device for automated actions.
- **Remote:** A physical ESP32-based IR transceiver.
- **VirtualDevice:** A software representation of a device controlled via IR (e.g., "Sony TV").
- **VirtualDeviceCommands:** Stores specific IR protocol data, hex codes, and raw timing data for various operations (Power, Volume, etc.).
- **CommandTemplate:** Pre-defined IR codes for common brands to simplify setup.

### Schema Snippet:
```java
// Device Entity
@Entity
public class Device 
{
     @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long deviceId;
     private String deviceName;
     private String deviceStatus; // ON/OFF
     private int pinNumber; // GPIO on ESP32
     @ManyToOne @JoinColumn(name = "microcontroller_id")
     private Microcontroller controller;
}
```

---

## 📂 Detailed Folder Structure

```text
src/main/java/com/automationSystem/system/
├── config/                 # Configuration classes
│   ├── MqttConfig.java     # MQTT Connection and Channel beans
│   ├── SecurityConfig.java # Spring Security & WebAuthn setup
│   └── DatabaseSeeder.java # Initial data for CommandTemplates
├── controller/             # REST Endpoints
│   ├── AdminController.java
│   ├── RemoteController.java
│   └── SwitchDeviceController.java
├── entity/                 # JPA Entities (Database Models)
│   ├── User.java, Device.java, Sensor.java...
│   └── VirtualDeviceCommands.java
├── repository/             # Spring Data JPA Repositories
├── DataTransferObjects/    # Request/Response POJOs (DTOs)
└── service/                # Business Logic
    ├── SwitchService.java  # Logic for GPIO switching
    ├── RemoteService.java  # IR Learning and Sending logic
    ├── MqttGateway.java    # Outbound MQTT interface
    └── MqttSubscriber.java # Inbound MQTT message handling
```

---

## 🛠 Core Services Implementation

### 1. MQTT Integration (`MqttConfig.java`)
The system uses Spring Integration for MQTT. It defines an outbound channel for sending commands and an inbound adapter for receiving status updates.

```java
@Bean
public MessageProducer inbound() 
{
     MqttPahoMessageDrivenChannelAdapter adapter =
         new MqttPahoMessageDrivenChannelAdapter("clientSide", factory, "status/ir/+/result");
     adapter.setOutputChannel(mqttInputChannel());
     return adapter;
}
```

### 2. Remote Service (`RemoteService.java`)
This service handles the discovery of IR protocols and sending stored IR commands to the ESP32.

```java
public ResponseEntity<?> sendCommand(SendCommandRequest request) 
{
     // Retrieve stored hex code and protocol
     VirtualDeviceCommands command = findCommand(request);
     
     ObjectNode mqttPayLoad = objectMapper.createObjectNode();
     mqttPayLoad.put("op", "SEND");
     mqttPayLoad.put("p", command.getProtocol());
     mqttPayLoad.put("hex", command.getHexCode());
     mqttPayLoad.put("bits", command.getBits());

     String topic = "commands/ir/" + macAddress + "/send";
     mqttGateway.sendToMqtt(topic, mqttPayLoad.toString());
    
     return ResponseEntity.ok("Signal sent successfully");
}
```

### 3. Switch Service (`SwitchService.java`)
Manages the state of devices connected directly to GPIO pins.

```java
public ResponseEntity<?> switchDevice(DeviceSwitchRequest request) 
{
     String topic = "home/devices/" + macAddress + "/command";
     String payload = String.format("{\"pin\": %d, \"status\": \"%s\"}", pinNumber, status);
    
     mqttGateway.sendToMqtt(topic, payload);
    
     device.setDeviceStatus(status);
     deviceRepository.save(device);
     return ResponseEntity.ok("Command sent: " + status);
}
```

---

## 🛠 Getting Started

### Prerequisites
- Java 21
- Maven
- MQTT Broker (e.g., Mosquitto)
- `yt-dlp` (for YouTube streaming features)

### Installation
1. Clone the repository.
2. Configure `application.properties` with your database and MQTT credentials.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## 🔮 Future Scope
- **Mobile App:** Integration with a React Native or Flutter app.
- **AI Automation:** Use sensor data patterns to automate lighting and cooling.
- **Voice Control:** NLP integration for voice control.
