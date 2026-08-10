package com.example.geartrackapi.controller.employee;

import org.springframework.data.domain.Page;
import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.controller.employee.dto.EmployeeUrlopDaysDto;
import com.example.geartrackapi.controller.employee.dto.VacationSummaryDto;
import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.service.EmployeeCrudService;
import com.example.geartrackapi.service.EmployeeUrlopDaysService;
import com.example.geartrackapi.service.ToolCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SUPER_USER')")
public class EmployeeController {

    private final EmployeeCrudService employeeCrudService;
    private final ToolCrudService toolCrudService;
    private final EmployeeUrlopDaysService employeeUrlopDaysService;
    
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> findAllEmployees(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        log.info("[findAllEmployees] Getting all employees with pagination: {} and search: {}", pageable, search);
        return ResponseEntity.ok(employeeCrudService.findAllEmployees(search, pageable));
    }
    
    @GetMapping("/{id}")
    public EmployeeDto findEmployeeById(@PathVariable UUID id) {
        log.info("[findEmployeeById] Getting employee with UUID: {}", id);
        return employeeCrudService.findEmployeeById(id);
    }
    
    @PostMapping
    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto) {
        log.info("[createEmployee] Creating employee with name: {} {}", employeeDto.getFirstName(), employeeDto.getLastName());
        return employeeCrudService.createEmployee(employeeDto);
    }
    
    @PutMapping
    public EmployeeDto updateEmployee(@RequestBody EmployeeDto employeeDto) {
        log.info("[updateEmployee] Updating employee with UUID: {}", employeeDto.getUuid());
        return employeeCrudService.updateEmployee(employeeDto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable UUID id) {
        log.info("[deleteEmployee] Deleting employee with UUID: {}", id);
        employeeCrudService.deleteEmployee(id);
    }
    
    @GetMapping("/{employeeId}/tools")
    public ResponseEntity<List<AssignToolDto>> getEmployeeTools(@PathVariable UUID employeeId) {
        log.info("[getEmployeeTools] Getting tools for employee {}", employeeId);
        return ResponseEntity.ok(toolCrudService.getToolsAssignedToEmployee(employeeId));
    }

    @GetMapping("/{employeeId}/vacation-summary")
    public ResponseEntity<VacationSummaryDto> getVacationSummary(@PathVariable UUID employeeId) {
        log.info("[getVacationSummary] Getting vacation summary for employee {}", employeeId);
        return ResponseEntity.ok(employeeUrlopDaysService.getVacationSummary(employeeId));
    }

    @PostMapping("/{employeeId}/urlop-days")
    public ResponseEntity<List<EmployeeUrlopDaysDto>> saveUrlopDays(
            @PathVariable UUID employeeId,
            @RequestBody List<EmployeeUrlopDaysDto> urlopDaysList) {
        log.info("[saveUrlopDays] Saving urlop days for employee {}: {}", employeeId, urlopDaysList);
        return ResponseEntity.ok(employeeUrlopDaysService.saveMultipleUrlopDays(employeeId, urlopDaysList));
    }
}