package com.automationSystem.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.User;

public interface UserRepository extends JpaRepository<User,Long>
{
     boolean existsByUsername(String username);

     Optional<User> findByUsername(String username);

    
}
