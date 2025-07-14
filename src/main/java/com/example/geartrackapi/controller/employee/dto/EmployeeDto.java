package com.example.geartrackapi.controller.employee.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class EmployeeDto {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private BigDecimal hourlyRate;
}