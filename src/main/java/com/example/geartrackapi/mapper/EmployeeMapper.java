package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.employee.dto.EmployeeDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.Role;
import com.example.geartrackapi.security.SecurityUser;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    
    public EmployeeDto toDto(Employee employee) {
        boolean isAdmin = isCurrentUserAdmin();
        return EmployeeDto.builder()
                .uuid(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .hourlyRate(isAdmin ? employee.getHourlyRate() : null)
                .build();
    }
    
    public Employee toEntity(EmployeeDto dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .hourlyRate(isCurrentUserAdmin() ? dto.getHourlyRate() : null)
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }
    
    public Employee updateEntity(Employee existing, EmployeeDto dto) {
        return Employee.builder()
                .id(existing.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .hourlyRate(isCurrentUserAdmin() ? dto.getHourlyRate() : existing.getHourlyRate())
                .organizationId(existing.getOrganizationId())
                .build();
    }
    
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getRole() == Role.ADMIN;
        }
        return false;
    }
}