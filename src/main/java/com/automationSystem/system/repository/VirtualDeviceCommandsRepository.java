package com.automationSystem.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.VirtualDeviceCommands;
import java.util.List;


public interface VirtualDeviceCommandsRepository extends JpaRepository<VirtualDeviceCommands, Long>
{
     List<VirtualDeviceCommands> findByProtocol(String protocol);
}
