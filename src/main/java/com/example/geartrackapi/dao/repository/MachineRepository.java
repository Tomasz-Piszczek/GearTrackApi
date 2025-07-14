package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID> {
    List<Machine> findByUserId(UUID userId);
    List<Machine> findByUserIdAndEmployeeId(UUID userId, UUID employeeId);
    List<Machine> findByUserIdAndNameContainingIgnoreCase(UUID userId, String name);
    List<Machine> findByUserIdAndFactoryNumber(UUID userId, String factoryNumber);
}