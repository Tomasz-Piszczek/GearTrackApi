package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.PayrollRecord;
import com.example.geartrackapi.dao.model.PayrollDeduction;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.PayrollRecordRepository;
import com.example.geartrackapi.dao.repository.PayrollDeductionRepository;
import com.example.geartrackapi.mapper.PayrollMapper;
import com.example.geartrackapi.mapper.PayrollDeductionMapper;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PayrollDeductionMapper payrollDeductionMapper;
    
    public List<PayrollRecordDto> getPayrollRecords(Integer year, Integer month) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndOrganizationIdAndHiddenFalseOrderByEmployeeId(year, month, organizationId);
        List<Employee> allEmployees = employeeRepository.findByOrganizationIdAndHiddenFalse(organizationId);

        Map<UUID, PayrollRecord> recordMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));

        return allEmployees.stream()
                .map(employee -> {
                    PayrollRecord record = recordMap.get(employee.getId());

                    if (record != null) {
                        PayrollRecordDto dto = payrollMapper.toDto(record, employee);
                        dto.setDeductions(calculateTotalDeductions(record.getId()));
                        return dto;
                    }

                    return PayrollRecordDto.builder()
                            .employeeId(employee.getId().toString())
                            .employeeName(employee.getFirstName() + " " + employee.getLastName())
                            .hourlyRate(employee.getHourlyRate())
                            .hoursWorked(BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void createOrUpdatePayrollRecords(List<PayrollRecordDto> records, Integer year, Integer month) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        for (PayrollRecordDto dto : records) {
            if (dto.getPayrollRecordId() != null) {
                updatePayrollRecord(dto, organizationId);
            } else {
                createPayrollRecord(dto, year, month, organizationId);
            }
        }
    }

    private void createPayrollRecord(PayrollRecordDto dto, Integer year, Integer month, UUID organizationId) {
        PayrollRecord payrollRecord = payrollMapper.toEntity(dto, year, month);
        PayrollRecord savedRecord = payrollRecordRepository.save(payrollRecord);

        if (dto.getPayrollDeductions() != null && !dto.getPayrollDeductions().isEmpty()) {
            List<PayrollDeduction> deductions = dto.getPayrollDeductions().stream()
                    .map(deductionDto -> payrollDeductionMapper.toEntity(savedRecord.getId().toString(), deductionDto))
                    .collect(Collectors.toList());
            payrollDeductionRepository.saveAll(deductions);
        }
    }

    private void updatePayrollRecord(PayrollRecordDto dto, UUID organizationId) {
        PayrollRecord existingRecord = payrollRecordRepository.findById(UUID.fromString(dto.getPayrollRecordId()))
                .orElseThrow();

        existingRecord.setHourlyRate(dto.getHourlyRate());
        existingRecord.setHoursWorked(dto.getHoursWorked());
        existingRecord.setBonus(dto.getBonus());
        existingRecord.setSickLeavePay(dto.getSickLeavePay());
        existingRecord.setDeductions(dto.getDeductions());
        existingRecord.setDeductionsNote(dto.getDeductionsNote());
        existingRecord.setBankTransfer(dto.getBankTransfer());
        existingRecord.setCashAmount(dto.getCashAmount());

        PayrollRecord savedRecord = payrollRecordRepository.save(existingRecord);

        List<PayrollDeduction> existingDeductions = payrollDeductionRepository.findByPayrollRecordIdAndOrganizationIdAndHiddenFalse(savedRecord.getId(), organizationId);
        existingDeductions.forEach(deduction -> deduction.setHidden(true));
        payrollDeductionRepository.saveAll(existingDeductions);

        if (dto.getPayrollDeductions() != null && !dto.getPayrollDeductions().isEmpty()) {
            List<PayrollDeduction> deductions = dto.getPayrollDeductions().stream()
                    .map(deductionDto -> payrollDeductionMapper.toEntity(savedRecord.getId().toString(), deductionDto))
                    .collect(Collectors.toList());
            payrollDeductionRepository.saveAll(deductions);
        }
    }

    private BigDecimal calculateTotalDeductions(UUID payrollRecordId) {

        return payrollDeductionRepository.findByPayrollRecordIdAndHiddenFalse(payrollRecordId)
                .stream()
                .map(deduction -> deduction.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<String> getAllCategories() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return payrollDeductionRepository.findDistinctCategoriesByOrganizationId(organizationId);
    }

    @Transactional
    public void deleteCategory(String category) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<PayrollDeduction> deductionsToDelete = payrollDeductionRepository
                .findByCategoryAndOrganizationIdAndHiddenFalse(category, organizationId);

        deductionsToDelete.forEach(deduction -> deduction.setHidden(true));
        payrollDeductionRepository.saveAll(deductionsToDelete);
    }

    public List<PayrollDeductionDto> getEmployeeDeductions(UUID employeeId) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<PayrollDeduction> deductions = payrollDeductionRepository
                .findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId);

        return deductions.stream()
                .map(payrollDeductionMapper::toDto)
                .collect(Collectors.toList());
    }

}