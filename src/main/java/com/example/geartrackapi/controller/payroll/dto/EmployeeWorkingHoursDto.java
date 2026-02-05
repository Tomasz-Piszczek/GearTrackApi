package com.example.geartrackapi.controller.payroll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWorkingHoursDto {
    private String employeeName;
    private Integer year;
    private Integer month;
    private BigDecimal hours;
    private List<DailyHoursDto> dailyHours;
    private List<DailyUrlopDto> dailyUrlopy;
    private List<LocalDate> conflictDates;
}
