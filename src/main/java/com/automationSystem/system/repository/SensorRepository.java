package com.automationSystem.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.Sensor;
public interface SensorRepository extends JpaRepository<Sensor, Object> 
{
     //Optional<Sensor> findByDeviceId(String deviceId);

     boolean existsBySensorType(String sensorType);

     Optional<Sensor> findBySensorType(String sensorType);

     // Find all sensors belonging to a specific ESP32
    
     // Find all ACTIVE sensors
     List<Sensor> findByStatus(String status);

}
