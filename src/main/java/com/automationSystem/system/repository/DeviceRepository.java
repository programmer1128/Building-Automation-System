package com.automationSystem.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.Device;
import com.automationSystem.system.entity.Microcontroller;
public interface DeviceRepository extends JpaRepository<Device, Long>
{
     //we have to include the methods here to find the device by id, name, status type
     boolean existsByDeviceId(Long deviceId);

     Optional<Device> findByDeviceId(Long deviceId);

     boolean existsByDeviceName(String deviceName);

     Optional<Device> findByDeviceName(String deviceName); 

     boolean existsByDeviceType(String deviceType); 

     Optional<Device> findByDeviceType(String deviceType); 

     boolean existsByDeviceStatus(String deviceStatus); 

     Optional<Device> findByDeviceStatus(String deviceStatus);

     Optional<Device> findByDeviceNameAndController(String name, Microcontroller microcontroller);

    // int countByMicrocontrollerId(Long microcontrollerId);

}
