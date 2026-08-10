package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.dao.model.Tool;
import com.example.geartrackapi.dao.model.ToolGroup;
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
                .groupId(tool.getToolGroup() != null ? tool.getToolGroup().getId() : null)
                .groupName(tool.getToolGroup() != null ? tool.getToolGroup().getName() : null)
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
                .groupId(tool.getToolGroup() != null ? tool.getToolGroup().getId() : null)
                .groupName(tool.getToolGroup() != null ? tool.getToolGroup().getName() : null)
                .build();
    }

    public Tool toEntity(ToolDto dto, ToolGroup toolGroup) {
        return Tool.builder()
                .name(dto.getName())
                .factoryNumber(dto.getFactoryNumber())
                .quantity(dto.getQuantity())
                .value(dto.getValue())
                .toolGroup(toolGroup)
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }

    public void updateEntity(Tool existing, ToolDto dto, ToolGroup toolGroup) {
        existing.setName(dto.getName());
        existing.setFactoryNumber(dto.getFactoryNumber());
        existing.setQuantity(dto.getQuantity());
        existing.setValue(dto.getValue());
        existing.setToolGroup(toolGroup);
    }
}
