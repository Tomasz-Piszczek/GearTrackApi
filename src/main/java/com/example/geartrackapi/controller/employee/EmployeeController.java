package com.example.geartrackapi.controller.employee;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.service.EmployeeCrudService;
import com.example.geartrackapi.service.ToolCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeCrudService employeeCrudService;
    private final ToolCrudService toolCrudService;
    
    @GetMapping
    public ResponseEntity<PagedResponse<EmployeeDto>> findAllEmployees(Pageable pageable) {
        log.info("[findAllEmployees] Getting all employees with pagination: {}", pageable);
        PagedResponse<EmployeeDto> employees = employeeCrudService.findAllEmployees(pageable);
        return ResponseEntity.ok(employees);
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
    
    @PostMapping("/{employeeId}/assign-tool")
    public ResponseEntity<AssignToolDto> assignToolToEmployee(
            @PathVariable UUID employeeId,
            @RequestBody AssignToolDto assignToolDto
    ) {
        log.info("[assignToolToEmployee] Assigning tool {} to employee {}", 
                assignToolDto.getToolId(), employeeId);
        
        // Set the employee ID from the path variable
        assignToolDto.setEmployeeId(employeeId);
        
        AssignToolDto result = toolCrudService.assignToolToEmployee(assignToolDto);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{employeeId}/tools")
    public ResponseEntity<List<AssignToolDto>> getEmployeeTools(@PathVariable UUID employeeId) {
        log.info("[getEmployeeTools] Getting tools for employee {}", employeeId);
        
        List<AssignToolDto> tools = toolCrudService.getToolsAssignedToEmployee(employeeId);
        return ResponseEntity.ok(tools);
    }
}