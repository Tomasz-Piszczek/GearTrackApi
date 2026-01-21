package com.example.geartrackapi.controller.payroll;

import com.example.geartrackapi.controller.payroll.dto.PayrollDeductionDto;
import com.example.geartrackapi.controller.payroll.dto.PayrollRecordDto;
import com.example.geartrackapi.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PayrollController {
    
    private final PayrollService payrollService;
    
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<PayrollRecordDto>> getPayrollRecords(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        log.info("[getPayrollRecords] Getting payroll records for {}/{}", year, month);
        return ResponseEntity.ok(payrollService.getPayrollRecords(year, month));
    }
    
    @PostMapping("/{year}/{month}")
    public ResponseEntity<Void> savePayrollRecords(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @RequestBody List<PayrollRecordDto> records) {
        log.info("[savePayrollRecords] Saving {} payroll records for {}/{}", records.size(), year, month);
        payrollService.createOrUpdatePayrollRecords(records, year, month);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("[getCategories] Getting all payroll deduction categories");
        return ResponseEntity.ok(payrollService.getAllCategories());
    }

    @DeleteMapping("/categories/{category}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String category) {
        log.info("[deleteCategory] Deleting category: {}", category);
        payrollService.deleteCategory(category);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employees/{employeeId}/deductions")
    public ResponseEntity<List<PayrollDeductionDto>> getEmployeeDeductions(@PathVariable UUID employeeId) {
        log.info("[getEmployeeDeductions] Getting deductions for employee: {}", employeeId);
        return ResponseEntity.ok(payrollService.getEmployeeDeductions(employeeId));
    }

}