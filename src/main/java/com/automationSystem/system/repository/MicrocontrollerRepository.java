package com.automationSystem.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.Microcontroller;

public interface MicrocontrollerRepository extends JpaRepository<Microcontroller, Object> 
{
     Optional<Microcontroller> findByMacAddress(String macAddress); 

   //  Optional<User> findByUserMacAddress(String macAddress);
}
