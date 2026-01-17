package com.example.geartrackapi.controller.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayrollDeductionDto {
    private String id;
    private String category;
    private String note;
    private BigDecimal amount;
}