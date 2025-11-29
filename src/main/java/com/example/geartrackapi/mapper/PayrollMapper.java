package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.PayrollRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PayrollMapper {
    
    public PayrollRecordDto toDto(PayrollRecord entity, Employee employee) {
        return PayrollRecordDto.builder()
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
                .build();
    }
    
    public PayrollRecord toEntity(PayrollRecordDto dto, Integer year, Integer month) {
        PayrollRecord entity = new PayrollRecord();
        entity.setEmployeeId(UUID.fromString(dto.getEmployeeId()));
        entity.setYear(year);
        entity.setMonth(month);
        entity.setHourlyRate(dto.getHourlyRate());
        entity.setHoursWorked(dto.getHoursWorked());
        entity.setBonus(dto.getBonus());
        entity.setSickLeavePay(dto.getSickLeavePay());
        entity.setDeductions(dto.getDeductions());
        entity.setDeductionsNote(dto.getDeductionsNote());
        entity.setBankTransfer(dto.getBankTransfer());
        entity.setCashAmount(dto.getCashAmount());
        return entity;
    }
}