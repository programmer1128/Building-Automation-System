package com.automationSystem.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automationSystem.system.entity.Remote;
import com.automationSystem.system.entity.User;
import java.util.Optional;
import java.util.List;


public interface RemoteRepository extends JpaRepository<Remote,Long>
{
     boolean existsByRemoteName(String remoteName);

     Optional<Remote> findByRemoteName(String remoteName);


     boolean existsByMacAddress(String macAddress);  

     Optional<Remote> findByMacAddress(String macAddress);


      List<Remote> findByOwner(User owner);
}
