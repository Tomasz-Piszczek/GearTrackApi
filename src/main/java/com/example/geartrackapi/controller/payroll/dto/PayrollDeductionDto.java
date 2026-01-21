package com.example.geartrackapi.controller.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayrollDeductionDto {
    private String id;
    private String category;
    private String note;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}