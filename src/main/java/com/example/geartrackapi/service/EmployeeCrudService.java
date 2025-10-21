package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.mapper.EmployeeMapper;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeCrudService {
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    
    public PagedResponse<EmployeeDto> findAllEmployees(Pageable pageable) {
        log.debug("[findAllEmployees] Getting paginated employees for authenticated user");
        Page<Employee> employeePage = employeeRepository.findByUserId(SecurityUtils.authenticatedUserId(), pageable);
        
        List<EmployeeDto> employeeDtos = employeePage.getContent()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        
        return PagedResponse.of(
                employeeDtos,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isFirst(),
                employeePage.isLast(),
                employeePage.isEmpty()
        );
    }
    
    public EmployeeDto findEmployeeById(UUID id) {
        log.debug("[findEmployeeById] Getting employee with UUID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));

        return employeeMapper.toDto(employee);
    }
    
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        log.debug("[createEmployee] Creating employee with name: {} {}", employeeDto.getFirstName(), employeeDto.getLastName());
        UUID userId = SecurityUtils.authenticatedUserId();
        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setUserId(userId);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }
    
    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        log.debug("[updateEmployee] Updating employee with UUID: {}", employeeDto.getUuid());
        Employee employee = employeeRepository.findById(employeeDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + employeeDto.getUuid()));

        employeeMapper.updateEntity(employee, employeeDto);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }
    
    public void deleteEmployee(UUID id) {
        log.debug("[deleteEmployee] Deleting employee with UUID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));
        
        employeeRepository.delete(employee);
    }
}