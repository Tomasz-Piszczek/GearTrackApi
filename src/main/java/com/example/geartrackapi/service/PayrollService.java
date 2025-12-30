package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.PayrollRecord;
import com.example.geartrackapi.dao.model.PayrollDeduction;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.PayrollRecordRepository;
import com.example.geartrackapi.dao.repository.PayrollDeductionRepository;
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
    private final PayrollDeductionRepository payrollDeductionRepository;
    
    public List<PayrollRecordDto> getPayrollRecords(Integer year, Integer month) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndOrganizationIdAndHiddenFalseOrderByEmployeeId(year, month, organizationId);
        List<Employee> allEmployees = employeeRepository.findByOrganizationIdAndHiddenFalse(organizationId);
        
        Map<UUID, PayrollRecord> recordMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));
        
        return allEmployees.stream()
                .map(employee -> {
                    PayrollRecord record = recordMap.get(employee.getId());
                    BigDecimal hoursWorked = BigDecimal.ZERO;
                    
                    if (record != null) {
                        PayrollRecordDto dto = payrollMapper.toDto(record, employee);
                        dto.setHoursWorked(hoursWorked);
                        dto.setDeductions(calculateTotalDeductions(record.getId()));
                        return dto;
                    }
                    
                    return PayrollRecordDto.builder()
                            .employeeId(employee.getId().toString())
                            .employeeName(employee.getFirstName() + " " + employee.getLastName())
                            .hourlyRate(employee.getHourlyRate())
                            .hoursWorked(hoursWorked)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    public void createOrUpdatePayrollRecords(List<PayrollRecordDto> records, Integer year, Integer month) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndOrganizationIdAndHiddenFalseOrderByEmployeeId(year, month, organizationId);
        Map<UUID, PayrollRecord> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));
        
        List<PayrollRecord> entities = records.stream()
                .map(dto -> {
                    UUID employeeId = UUID.fromString(dto.getEmployeeId());
                    PayrollRecord existing = existingMap.get(employeeId);
                    
                    if (existing != null) {
                        PayrollRecord updated = payrollMapper.toEntity(dto, year, month);
                        updated.setId(existing.getId());
                        return updated;
                    } else {
                        return payrollMapper.toEntity(dto, year, month);
                    }
                })
                .collect(Collectors.toList());
        
        payrollRecordRepository.saveAll(entities);
    }
    
    private BigDecimal calculateTotalDeductions(UUID payrollRecordId) {

        return payrollDeductionRepository.findByPayrollRecordIdAndHiddenFalse(payrollRecordId)
                .stream()
                .map(deduction -> deduction.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}