package com.example.geartrackapi.controller.employee;

import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.service.EmployeeCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeCrudService employeeCrudService;
    
    @GetMapping
    public List<EmployeeDto> findAllEmployees() {
        log.info("[findAllEmployees] Getting all employees");
        return employeeCrudService.findAllEmployees();
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
}