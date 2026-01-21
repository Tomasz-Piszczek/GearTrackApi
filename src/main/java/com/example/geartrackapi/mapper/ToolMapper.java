package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.dao.model.Tool;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class ToolMapper {
    
    public ToolDto toDto(Tool tool) {
        return ToolDto.builder()
                .uuid(tool.getId())
                .name(tool.getName())
                .factoryNumber(tool.getFactoryNumber())
                .quantity(tool.getQuantity())
                .value(tool.getValue())
                .build();
    }
    
    public ToolDto toDto(Tool tool, Integer availableQuantity) {
        return ToolDto.builder()
                .uuid(tool.getId())
                .name(tool.getName())
                .factoryNumber(tool.getFactoryNumber())
                .quantity(tool.getQuantity())
                .value(tool.getValue())
                .availableQuantity(availableQuantity)
                .build();
    }
    
    public Tool toEntity(ToolDto dto) {
        return Tool.builder()
                .name(dto.getName())
                .factoryNumber(dto.getFactoryNumber())
                .quantity(dto.getQuantity())
                .value(dto.getValue())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }
    
    public Tool updateEntity(Tool existing, ToolDto dto) {
        return Tool.builder()
                .id(existing.getId())
                .name(dto.getName())
                .factoryNumber(dto.getFactoryNumber())
                .quantity(dto.getQuantity())
                .value(dto.getValue())
                .organizationId(existing.getOrganizationId())
                .build();
    }
}