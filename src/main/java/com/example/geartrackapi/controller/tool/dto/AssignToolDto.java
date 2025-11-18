package com.example.geartrackapi.controller.tool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AssignToolDto {
    private UUID uuid;
    private UUID employeeId;
    private UUID toolId;
    private Integer quantity;
    private String condition;
    private LocalDate assignedAt;
    private String employeeName;
    private String toolName;
}