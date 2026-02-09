package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.payroll.dto.EmployeeWorkingHoursDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollMapper payrollMapper;
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final PayrollDeductionMapper payrollDeductionMapper;
    private final CalculateWorkingHoursService calculateWorkingHoursService;

    public List<PayrollRecordDto> getPayrollRecords(Integer year, Integer month, String jwtToken) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<PayrollRecord> existingRecords = payrollRecordRepository.findByYearAndMonthAndOrganizationIdAndHiddenFalseOrderByEmployeeId(year, month, organizationId);
        List<Employee> allEmployees = employeeRepository.findByOrganizationIdAndHiddenFalse(organizationId);

        Map<UUID, PayrollRecord> recordMap = existingRecords.stream()
                .collect(Collectors.toMap(PayrollRecord::getEmployeeId, Function.identity()));

        List<String> employeeNames = allEmployees.stream()
                .map(employee -> employee.getFirstName() + " " + employee.getLastName())
                .collect(Collectors.toList());

        Map<String, CalculateWorkingHoursService.WorkingHoursData> workingHoursMap =
                calculateWorkingHoursService.calculateWorkingHours(employeeNames, year, month, jwtToken, organizationId);

        return allEmployees.stream()
                .map(employee -> {
                    String employeeName = employee.getFirstName() + " " + employee.getLastName();
                    PayrollRecord record = recordMap.get(employee.getId());
                    CalculateWorkingHoursService.WorkingHoursData workingHoursData = workingHoursMap.get(employeeName);

                    PayrollRecordDto dto;
                    if (record != null) {
                        dto = payrollMapper.toDto(record, employee);
                        dto.setDeductions(calculateTotalDeductions(record.getId()));

                        BigDecimal calculatedHours = workingHoursData.getTotalHours();
                        BigDecimal savedHours = record.getHoursWorked();

                        if (calculatedHours.compareTo(savedHours) != 0) {
                            dto.setHasDiscrepancy(true);
                            dto.setLastSavedHours(savedHours);
                            dto.setLastModifiedAt(record.getUpdatedAt());
                        } else {
                            dto.setHasDiscrepancy(false);
                        }

                        dto.setHoursWorked(calculatedHours);
                    } else {
                        dto = PayrollRecordDto.builder()
                                .employeeId(employee.getId().toString())
                                .employeeName(employeeName)
                                .hourlyRate(employee.getHourlyRate())
                                .hoursWorked(workingHoursData.getTotalHours())
                                .hasDiscrepancy(false)
                                .build();
                    }

                    dto.setDailyBreakdown(workingHoursData.getDailyBreakdown());
                    dto.setUrlopBreakdown(workingHoursData.getUrlopBreakdown());

                    return dto;
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
        payrollRecord.setHoursWorked(dto.getHoursWorked());
        PayrollRecord savedRecord = payrollRecordRepository.save(payrollRecord);

        if (!dto.getPayrollDeductions().isEmpty()) {
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
        existingRecord.setPaid(dto.getPaid());

        PayrollRecord savedRecord = payrollRecordRepository.save(existingRecord);

        List<PayrollDeduction> existingDeductions = payrollDeductionRepository.findByPayrollRecordIdAndOrganizationIdAndHiddenFalse(savedRecord.getId(), organizationId);
        existingDeductions.forEach(deduction -> deduction.setHidden(true));
        payrollDeductionRepository.saveAll(existingDeductions);

        if (!dto.getPayrollDeductions().isEmpty()) {
            List<PayrollDeduction> deductions = dto.getPayrollDeductions().stream()
                    .map(deductionDto -> payrollDeductionMapper.toEntity(savedRecord.getId().toString(), deductionDto))
                    .collect(Collectors.toList());
            payrollDeductionRepository.saveAll(deductions);
        }
    }

    private BigDecimal calculateTotalDeductions(UUID payrollRecordId) {
        return payrollDeductionRepository.findByPayrollRecordIdAndHiddenFalse(payrollRecordId)
                .stream()
                .map(PayrollDeduction::getAmount)
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

    public EmployeeWorkingHoursDto getEmployeeWorkingHours(String employeeName, Integer year, Integer month, String jwtToken) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Map<String, CalculateWorkingHoursService.WorkingHoursData> workingHoursMap =
                calculateWorkingHoursService.calculateWorkingHours(Collections.singletonList(employeeName), year, month, jwtToken, organizationId);

        CalculateWorkingHoursService.WorkingHoursData workingHoursData = workingHoursMap.get(employeeName);

        if (workingHoursData == null) {
            return EmployeeWorkingHoursDto.builder()
                    .totalHours(BigDecimal.ZERO)
                    .dailyBreakdown(Collections.emptyList())
                    .urlopBreakdown(Collections.emptyList())
                    .build();
        }

        return EmployeeWorkingHoursDto.builder()
                .totalHours(workingHoursData.getTotalHours())
                .dailyBreakdown(workingHoursData.getDailyBreakdown())
                .urlopBreakdown(workingHoursData.getUrlopBreakdown())
                .build();
    }
}
