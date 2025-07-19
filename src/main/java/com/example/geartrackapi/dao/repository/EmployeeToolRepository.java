package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.EmployeeTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeToolRepository extends JpaRepository<EmployeeTool, UUID> {
    List<EmployeeTool> findByUserId(UUID userId);
    List<EmployeeTool> findByUserIdAndEmployeeId(UUID userId, UUID employeeId);
    List<EmployeeTool> findByUserIdAndToolId(UUID userId, UUID toolId);
}