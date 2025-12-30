package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.dao.model.EmployeeTool;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class EmployeeToolMapper {
    
    public AssignToolDto toAssignToolDto(EmployeeTool employeeTool) {
        return AssignToolDto.builder()
                .uuid(employeeTool.getId())
                .employeeId(employeeTool.getEmployeeId())
                .toolId(employeeTool.getToolId())
                .quantity(employeeTool.getQuantity())
                .condition(employeeTool.getCondition())
                .assignedAt(employeeTool.getAssignedAt())
                .employeeName(employeeTool.getEmployee() != null ? 
                    employeeTool.getEmployee().getFirstName() + " " + employeeTool.getEmployee().getLastName() : null)
                .toolName(employeeTool.getTool() != null ? employeeTool.getTool().getName() : null)
                .toolPrice(employeeTool.getTool() != null ? employeeTool.getTool().getValue() : null)
                .toolFactoryNumber(employeeTool.getTool() != null ? employeeTool.getTool().getFactoryNumber() : null)
                .build();
    }
    
    public EmployeeTool toEntity(AssignToolDto dto) {
        return EmployeeTool.builder()
                .employeeId(dto.getEmployeeId())
                .toolId(dto.getToolId())
                .quantity(dto.getQuantity())
                .condition(dto.getCondition())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }
}