package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.dao.model.PayrollDeduction;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class PayrollDeductionMapper {
    
    public PayrollDeductionDto toDto(PayrollDeduction entity) {
        return PayrollDeductionDto.builder()
                .id(entity.getId().toString())
                .category(entity.getCategory())
                .note(entity.getNote())
                .amount(entity.getAmount())
                .build();
    }
    
    public PayrollDeduction toEntity(String payrollRecordId, PayrollDeductionDto dto) {
        return PayrollDeduction.builder()
                .payrollRecordId(java.util.UUID.fromString(payrollRecordId))
                .category(dto.getCategory())
                .note(dto.getNote())
                .amount(dto.getAmount())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }
    
    public PayrollDeduction updateEntity(PayrollDeduction existing, PayrollDeductionDto dto) {
        return PayrollDeduction.builder()
                .id(existing.getId())
                .payrollRecordId(existing.getPayrollRecordId())
                .category(dto.getCategory())
                .note(dto.getNote())
                .amount(dto.getAmount())
                .organizationId(existing.getOrganizationId())
                .build();
    }
}