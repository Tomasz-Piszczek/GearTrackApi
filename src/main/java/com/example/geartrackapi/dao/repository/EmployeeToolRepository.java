package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.EmployeeTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeToolRepository extends JpaRepository<EmployeeTool, UUID> {
    @Query("SELECT et FROM EmployeeTool et JOIN FETCH et.employee JOIN FETCH et.tool WHERE et.userId = :userId AND et.employeeId = :employeeId")
    List<EmployeeTool> findByUserIdAndEmployeeId(@Param("userId") UUID userId, @Param("employeeId") UUID employeeId);
    
    @Query("SELECT et FROM EmployeeTool et JOIN FETCH et.employee JOIN FETCH et.tool WHERE et.userId = :userId AND et.toolId = :toolId")
    List<EmployeeTool> findByUserIdAndToolId(@Param("userId") UUID userId, @Param("toolId") UUID toolId);

    @Query("SELECT COALESCE(SUM(et.quantity), 0) FROM EmployeeTool et WHERE et.userId = :userId AND et.toolId = :toolId")
    Integer getTotalAssignedQuantityByUserIdAndToolId(@Param("userId") UUID userId, @Param("toolId") UUID toolId);
    
    @Query("SELECT GREATEST(0, t.quantity - COALESCE(SUM(et.quantity), 0)) " +
           "FROM Tool t LEFT JOIN EmployeeTool et ON t.id = et.toolId AND et.userId = :userId " +
           "WHERE t.id = :toolId AND t.userId = :userId " +
           "GROUP BY t.id, t.quantity")
    Integer getAvailableQuantityByUserIdAndToolId(@Param("userId") UUID userId, @Param("toolId") UUID toolId);
}