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
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        Page<Employee> employeePage;
        
        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.findByOrganizationIdAndNameContaining(organizationId, search.trim(), pageable);
        } else {
            employeePage = employeeRepository.findByOrganizationIdAndHiddenFalse(organizationId, pageable);
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
        Employee employee = employeeMapper.toEntity(employeeDto);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }
    
    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        Employee existing = employeeRepository.findByIdAndHiddenFalse(employeeDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + employeeDto.getUuid()));
        Employee updated = employeeMapper.updateEntity(existing, employeeDto);
        return employeeMapper.toDto(employeeRepository.save(updated));
    }
    
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with UUID: " + id));
        employee.setHidden(true);
        employeeRepository.save(employee);
    }

}