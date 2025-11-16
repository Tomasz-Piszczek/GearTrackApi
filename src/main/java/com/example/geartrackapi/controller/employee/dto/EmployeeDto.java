package com.example.geartrackapi.controller.employee.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EmployeeDto {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private BigDecimal hourlyRate;
    private List<AssignedToolDto> assignedTools;
    private int totalAssignedTools;
}

@Data
@Builder
class AssignedToolDto {
    private UUID toolId;
    private String toolName;
    private String toolFactoryNumber;
    private String toolSize;
    private int assignedQuantity;
    private String condition;
    private String assignedAt;
}