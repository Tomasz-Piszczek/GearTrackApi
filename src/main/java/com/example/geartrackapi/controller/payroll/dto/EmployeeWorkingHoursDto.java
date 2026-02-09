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
public class EmployeeWorkingHoursDto {
    private BigDecimal totalHours;
    private List<DailyBreakdownDto> dailyBreakdown;
    private List<UrlopBreakdownDto> urlopBreakdown;
}
