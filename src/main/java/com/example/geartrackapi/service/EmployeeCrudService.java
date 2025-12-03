package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.mapper.EmployeeMapper;
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
public class EmployeeCrudService {
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    
    public Page<EmployeeDto> findAllEmployees(String search, Pageable pageable) {
        UUID userId = SecurityUtils.authenticatedUserId();
        Page<Employee> employeePage;
        
        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.findByUserIdAndNameContaining(userId, search.trim(), pageable);
        } else {
            employeePage = employeeRepository.findByUserIdAndHiddenFalse(userId, pageable);
        }
        
        List<EmployeeDto> employeeDtos = employeePage.getContent()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(employeeDtos, pageable, employeePage.getTotalElements());
    }
    
    public EmployeeDto findEmployeeById(UUID id) {
        Employee employee = employeeRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));
        return employeeMapper.toDto(employee);
    }
    
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        UUID userId = SecurityUtils.authenticatedUserId();
        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setUserId(userId);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }
    
    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        Employee employee = employeeRepository.findByIdAndHiddenFalse(employeeDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + employeeDto.getUuid()));
        employeeMapper.updateEntity(employee, employeeDto);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }
    
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));
        employee.setHidden(true);
        employeeRepository.save(employee);
    }
    
    public EmployeeDto restoreEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));
        
        if (Boolean.FALSE.equals(employee.getHidden())) {
            throw new IllegalStateException("Employee with UUID: " + id + " is not deleted");
        }
        
        employee.setHidden(false);
        Employee restored = employeeRepository.save(employee);
        return employeeMapper.toDto(restored);
    }
}