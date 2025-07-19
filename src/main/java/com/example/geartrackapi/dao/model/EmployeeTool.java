package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_tools")
@Getter
@Setter
public class EmployeeTool extends BaseEntity {
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "tool_id", nullable = false)
    private UUID toolId;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDate assignedAt;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "condition", nullable = false)
    private String condition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", insertable = false, updatable = false)
    private Tool tool;
}