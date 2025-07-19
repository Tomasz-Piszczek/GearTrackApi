package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.dao.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    
    public EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .uuid(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .hourlyRate(employee.getHourlyRate())
                .build();
    }
    
    public Employee toEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setHourlyRate(dto.getHourlyRate());
        return employee;
    }
    
    public void updateEntity(Employee employee, EmployeeDto dto) {
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setHourlyRate(dto.getHourlyRate());
    }
}