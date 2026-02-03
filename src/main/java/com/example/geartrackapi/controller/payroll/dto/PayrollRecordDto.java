package com.example.geartrackapi.controller.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PayrollRecordDto {
    private String payrollRecordId;
    private String employeeId;
    private String employeeName;
    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;
    private BigDecimal bonus;
    private BigDecimal sickLeavePay;
    private BigDecimal deductions;
    private String deductionsNote;
    private BigDecimal bankTransfer;
    private BigDecimal cashAmount;
    private Boolean paid;
    private List<PayrollDeductionDto> payrollDeductions;
}