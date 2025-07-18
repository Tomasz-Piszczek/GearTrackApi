package com.example.geartrackapi.controller.employee;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.service.EmployeeCrudService;
import com.example.geartrackapi.service.ToolCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PagedResponse<EmployeeDto>> findAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        log.info("[findAllEmployees] Getting all employees - page: {}, size: {}, sortBy: {}, direction: {}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<EmployeeDto> employees = employeeCrudService.findAllEmployees(pageable);
        
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/all")
    public List<EmployeeDto> findAllEmployeesNonPaginated() {
        log.info("[findAllEmployeesNonPaginated] Getting all employees without pagination");
        return employeeCrudService.findAllEmployeesNonPaginated();
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