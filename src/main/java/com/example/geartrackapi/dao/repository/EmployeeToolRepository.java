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
    @Query("SELECT et FROM EmployeeTool et JOIN FETCH et.employee JOIN FETCH et.tool WHERE et.organizationId = :organizationId AND et.employeeId = :employeeId AND et.hidden = false")
    List<EmployeeTool> findByOrganizationIdAndEmployeeId(@Param("organizationId") UUID organizationId, @Param("employeeId") UUID employeeId);
    
    @Query("SELECT et FROM EmployeeTool et JOIN FETCH et.employee JOIN FETCH et.tool WHERE et.organizationId = :organizationId AND et.toolId = :toolId AND et.hidden = false")
    List<EmployeeTool> findByOrganizationIdAndToolId(@Param("organizationId") UUID organizationId, @Param("toolId") UUID toolId);

    @Query("SELECT COALESCE(SUM(et.quantity), 0) FROM EmployeeTool et WHERE et.organizationId = :organizationId AND et.toolId = :toolId AND et.hidden = false")
    Integer getTotalAssignedQuantityByOrganizationIdAndToolId(@Param("organizationId") UUID organizationId, @Param("toolId") UUID toolId);
    
    @Query("SELECT GREATEST(0, t.quantity - COALESCE(SUM(et.quantity), 0)) " +
           "FROM Tool t LEFT JOIN EmployeeTool et ON t.id = et.toolId AND et.organizationId = :organizationId AND et.hidden = false " +
           "WHERE t.id = :toolId AND t.organizationId = :organizationId AND t.hidden = false " +
           "GROUP BY t.id, t.quantity")
    Integer getAvailableQuantityByOrganizationIdAndToolId(@Param("organizationId") UUID organizationId, @Param("toolId") UUID toolId);
    
    List<EmployeeTool> findByOrganizationIdAndHiddenFalse(UUID organizationId);
    
}