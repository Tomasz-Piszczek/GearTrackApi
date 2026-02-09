package com.example.geartrackapi.controller.payroll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBreakdownDto {
    private LocalDate date;
    private BigDecimal actualHours;
    private BigDecimal roundedHours;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
