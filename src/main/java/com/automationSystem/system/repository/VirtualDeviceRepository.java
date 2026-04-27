package com.automationSystem.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.Remote;
import com.automationSystem.system.entity.VirtualDevice;


public interface VirtualDeviceRepository extends JpaRepository<VirtualDevice, Object>
{
     boolean existsByDeviceName(String deviceName);

     Optional<VirtualDevice> findByDeviceName(String deviceName);

     boolean existsByRemote(Remote remote);

     Optional<VirtualDevice> findByRemote(Remote remote);

     Optional<VirtualDevice> findByIsNew(String isNew);
}


