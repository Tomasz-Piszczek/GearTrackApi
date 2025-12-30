package com.example.geartrackapi.controller.payroll;

import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.service.PayrollDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payroll-deductions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PayrollDeductionController {
    
    private final PayrollDeductionService payrollDeductionService;
    
    @PostMapping
    public ResponseEntity<PayrollDeductionDto> createDeduction(@RequestBody PayrollDeductionDto deductionDto) {
        log.info("[createDeduction] Creating deduction for payroll record: {}", deductionDto.getPayrollRecordId());
        return ResponseEntity.ok(payrollDeductionService.createDeduction(deductionDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PayrollDeductionDto> updateDeduction(@PathVariable UUID id, @RequestBody PayrollDeductionDto deductionDto) {
        log.info("[updateDeduction] Updating deduction with ID: {}", id);
        return ResponseEntity.ok(payrollDeductionService.updateDeduction(id, deductionDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeduction(@PathVariable UUID id) {
        log.info("[deleteDeduction] Deleting deduction with ID: {}", id);
        payrollDeductionService.deleteDeduction(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<PayrollDeductionDto>> getEmployeeDeductions(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        log.info("[getEmployeeDeductions] Getting deductions for employee: {}, category: {}", employeeId, category);
        return ResponseEntity.ok(payrollDeductionService.getEmployeeDeductions(employeeId, category, pageable));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("[getCategories] Getting distinct categories");
        return ResponseEntity.ok(payrollDeductionService.getDistinctCategories());
    }
}