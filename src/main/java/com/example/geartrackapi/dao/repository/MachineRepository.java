package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID> {
    List<Machine> findByUserId(UUID userId);
    Page<Machine> findByUserId(UUID userId, Pageable pageable);
    
    List<Machine> findByUserIdAndEmployeeId(UUID userId, UUID employeeId);
    Page<Machine> findByUserIdAndEmployeeId(UUID userId, UUID employeeId, Pageable pageable);
    
    List<Machine> findByUserIdAndNameContainingIgnoreCase(UUID userId, String name);
    Page<Machine> findByUserIdAndNameContainingIgnoreCase(UUID userId, String name, Pageable pageable);
    
    List<Machine> findByUserIdAndFactoryNumber(UUID userId, String factoryNumber);
    Page<Machine> findByUserIdAndFactoryNumber(UUID userId, String factoryNumber, Pageable pageable);
}