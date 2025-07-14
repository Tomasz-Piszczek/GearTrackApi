package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.dao.model.EmployeeTool;
import org.springframework.stereotype.Component;

@Component
public class EmployeeToolMapper {
    
    public AssignToolDto toAssignToolDto(EmployeeTool employeeTool) {
        return AssignToolDto.builder()
                .uuid(employeeTool.getUuid())
                .employeeId(employeeTool.getEmployeeId())
                .toolId(employeeTool.getToolId())
                .quantity(employeeTool.getQuantity())
                .condition(employeeTool.getCondition())
                .assignedAt(employeeTool.getAssignedAt())
                .build();
    }
    
    public EmployeeTool toEntity(AssignToolDto dto) {
        EmployeeTool employeeTool = new EmployeeTool();
        employeeTool.setEmployeeId(dto.getEmployeeId());
        employeeTool.setToolId(dto.getToolId());
        employeeTool.setQuantity(dto.getQuantity());
        employeeTool.setCondition(dto.getCondition());
        employeeTool.setAssignedAt(dto.getAssignedAt());
        return employeeTool;
    }
}