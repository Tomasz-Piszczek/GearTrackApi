package com.example.geartrackapi.controller.payroll.dto;

import com.example.geartrackapi.dao.model.UrlopCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlopBreakdownDto {
    private UrlopCategory category;
    private BigDecimal totalHours;
    private BigDecimal rate;
}
