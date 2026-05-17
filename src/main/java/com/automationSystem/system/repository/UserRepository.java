package com.automationSystem.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.automationSystem.system.entity.User;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User,Long>
{
      boolean existsByUsername(String username);

      Optional<User> findByUsername(String username);

      @org.springframework.data.jpa.repository.Modifying
      @Transactional
      @Query("UPDATE User u SET u.deviceCount = u.deviceCount + 1 WHERE u.username = :username")
      void incrementDeviceCount(@Param("username") String username);
      //Optional<User> findByMicrocontrollers(Microcontroller microcontroller);

      @Query("SELECT u.deviceCount FROM User u WHERE u.username = :username")
      int getDeviceCountByUsername(@Param("username") String username);
}
