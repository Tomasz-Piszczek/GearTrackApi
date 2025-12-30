package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.PayrollRecord;
import com.example.geartrackapi.dao.model.PayrollDeduction;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PayrollMapper {
    
    public PayrollRecordDto toDto(PayrollRecord entity, Employee employee) {
        return PayrollRecordDto.builder()
                .payrollRecordId(entity.getId().toString())
                .employeeId(entity.getEmployeeId().toString())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .hourlyRate(entity.getHourlyRate())
                .hoursWorked(entity.getHoursWorked())
                .bonus(entity.getBonus())
                .sickLeavePay(entity.getSickLeavePay())
                .deductions(entity.getDeductions())
                .deductionsNote(entity.getDeductionsNote())
                .bankTransfer(entity.getBankTransfer())
                .cashAmount(entity.getCashAmount())
                .payrollDeductions(entity.getPayrollDeductions() != null ? 
                    entity.getPayrollDeductions().stream()
                        .filter(deduction -> !deduction.getHidden())
                        .map(this::payrollDeductionToDto)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }
    
    public PayrollRecord toEntity(PayrollRecordDto dto, Integer year, Integer month) {
        return PayrollRecord.builder()
                .employeeId(UUID.fromString(dto.getEmployeeId()))
                .year(year)
                .month(month)
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .hourlyRate(dto.getHourlyRate())
                .hoursWorked(dto.getHoursWorked())
                .bonus(dto.getBonus())
                .sickLeavePay(dto.getSickLeavePay())
                .deductions(dto.getDeductions())
                .deductionsNote(dto.getDeductionsNote())
                .bankTransfer(dto.getBankTransfer())
                .cashAmount(dto.getCashAmount())
                .build();
    }
    
    private PayrollDeductionDto payrollDeductionToDto(PayrollDeduction deduction) {
        return PayrollDeductionDto.builder()
                .id(deduction.getId().toString())
                .payrollRecordId(deduction.getPayrollRecordId().toString())
                .category(deduction.getCategory())
                .note(deduction.getNote())
                .amount(deduction.getAmount())
                .build();
    }
    

}