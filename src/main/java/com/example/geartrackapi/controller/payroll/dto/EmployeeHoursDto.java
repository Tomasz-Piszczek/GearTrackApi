package com.example.geartrackapi.controller.payroll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHoursDto {
    private String employeeName;
    private Integer year;
    private Integer month;
    private BigDecimal hours;
    private List<DailyHoursDto> dailyHours;
}
