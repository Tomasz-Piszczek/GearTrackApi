package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.PayrollRecord;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.PayrollRecordRepository;
import com.example.geartrackapi.mapper.PayrollMapper;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollService {
    
    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollMapper payrollMapper;
    
    public List<PayrollRecordDto> getPayrollRecords(Integer year, Integer month) {
        UUID userId = SecurityUtils.authenticatedUserId();
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndUserIdOrderByEmployeeId(year, month, userId);
        List<Employee> allEmployees = employeeRepository.findByUserId(userId);
        
        Map<UUID, PayrollRecord> recordMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));
        
        return allEmployees.stream()
                .map(employee -> {
                    PayrollRecord record = recordMap.get(employee.getId());
                    if (record != null) {
                        return payrollMapper.toDto(record, employee);
                    }
                    return PayrollRecordDto.builder()
                            .employeeId(employee.getId().toString())
                            .employeeName(employee.getFirstName() + " " + employee.getLastName())
                            .hourlyRate(employee.getHourlyRate())
                            .hoursWorked(BigDecimal.ZERO)
                            .bonus(BigDecimal.ZERO)
                            .sickLeavePay(BigDecimal.ZERO)
                            .deductions(BigDecimal.ZERO)
                            .deductionsNote(null)
                            .bankTransfer(BigDecimal.ZERO)
                            .cashAmount(BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    public void savePayrollRecords(List<PayrollRecordDto> records, Integer year, Integer month) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndUserIdOrderByEmployeeId(year, month, userId);
        Map<UUID, PayrollRecord> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));
        
        List<PayrollRecord> entities = records.stream()
                .map(dto -> {
                    UUID employeeId = UUID.fromString(dto.getEmployeeId());
                    PayrollRecord existing = existingMap.get(employeeId);
                    
                    if (existing != null) {
                        existing.setHourlyRate(dto.getHourlyRate());
                        existing.setHoursWorked(dto.getHoursWorked());
                        existing.setBonus(dto.getBonus());
                        existing.setSickLeavePay(dto.getSickLeavePay());
                        existing.setDeductions(dto.getDeductions());
                        existing.setDeductionsNote(dto.getDeductionsNote());
                        existing.setBankTransfer(dto.getBankTransfer());
                        existing.setCashAmount(dto.getCashAmount());
                        return existing;
                    } else {
                        PayrollRecord record = payrollMapper.toEntity(dto, year, month);
                        record.setUserId(userId);
                        return record;
                    }
                })
                .collect(Collectors.toList());
        
        payrollRecordRepository.saveAll(entities);
    }
}