package com.automationSystem.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.CommandTemplate;



public interface CommandTemplateRepository extends JpaRepository<CommandTemplate, Long>
{
         
}