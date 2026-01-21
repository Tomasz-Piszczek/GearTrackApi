package com.example.geartrackapi.controller.tool.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AssignToolDto {
    private UUID uuid;
    private Integer quantity;
    private String condition;
    private LocalDate assignedAt;
    private String employeeName;
    private String toolName;
    private BigDecimal toolPrice;
    private String toolFactoryNumber;
}