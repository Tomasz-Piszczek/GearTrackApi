package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.dao.model.PayrollDeduction;
import com.example.geartrackapi.dao.repository.PayrollDeductionRepository;
import com.example.geartrackapi.mapper.PayrollDeductionMapper;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollDeductionService {
    
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final PayrollDeductionMapper payrollDeductionMapper;
    
    public PayrollDeductionDto createDeduction(PayrollDeductionDto deductionDto) {
        PayrollDeduction deduction = payrollDeductionMapper.toEntity(deductionDto);
        PayrollDeduction saved = payrollDeductionRepository.save(deduction);
        return payrollDeductionMapper.toDto(saved);
    }
    
    public PayrollDeductionDto updateDeduction(UUID id, PayrollDeductionDto deductionDto) {
        PayrollDeduction existing = payrollDeductionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found with UUID: " + id));
        
        PayrollDeduction updated = payrollDeductionMapper.updateEntity(existing, deductionDto);
        PayrollDeduction saved = payrollDeductionRepository.save(updated);
        return payrollDeductionMapper.toDto(saved);
    }
    
    public void deleteDeduction(UUID id) {
        PayrollDeduction deduction = payrollDeductionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found with UUID: " + id));
        deduction.setHidden(true);
        payrollDeductionRepository.save(deduction);
    }
    //todo fix organization
    public Page<PayrollDeductionDto> getEmployeeDeductions(UUID employeeId, String category, Pageable pageable) {
        Page<PayrollDeduction> deductionsPage;
        
        if (category != null && !category.trim().isEmpty()) {
            deductionsPage = payrollDeductionRepository.findByEmployeeIdAndCategoryContainingIgnoreCaseAndOrganizationIdAndHiddenFalseOrderByCreatedAtDesc(employeeId, category.toUpperCase(),UUID.fromString("e0318ab7-3ca7-4787-8165-f14dc5fe465d"),pageable);
        } else {
            deductionsPage = payrollDeductionRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalseOrderByCreatedAtDesc(employeeId,UUID.fromString("e0318ab7-3ca7-4787-8165-f14dc5fe465d"), pageable);
        }
        
        List<PayrollDeductionDto> deductionDtos = deductionsPage.getContent()
                .stream()
                .map(payrollDeductionMapper::toDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(deductionDtos, pageable, deductionsPage.getTotalElements());
    }
    
    public List<String> getDistinctCategories() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return payrollDeductionRepository.findDistinctCategoriesByOrganizationIdAndHiddenFalse(organizationId);
    }
}