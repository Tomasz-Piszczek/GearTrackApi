package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.dao.model.Tool;
import org.springframework.stereotype.Component;

@Component
public class ToolMapper {
    
    public ToolDto toDto(Tool tool) {
        return ToolDto.builder()
                .uuid(tool.getId())
                .name(tool.getName())
                .factoryNumber(tool.getFactoryNumber())
                .size(tool.getSize())
                .quantity(tool.getQuantity())
                .value(tool.getValue())
                .build();
    }
    
    public Tool toEntity(ToolDto dto) {
        Tool tool = new Tool();
        tool.setName(dto.getName());
        tool.setFactoryNumber(dto.getFactoryNumber());
        tool.setSize(dto.getSize());
        tool.setQuantity(dto.getQuantity());
        tool.setValue(dto.getValue());
        return tool;
    }
    
    public void updateEntity(Tool tool, ToolDto dto) {
        tool.setName(dto.getName());
        tool.setFactoryNumber(dto.getFactoryNumber());
        tool.setSize(dto.getSize());
        tool.setQuantity(dto.getQuantity());
        tool.setValue(dto.getValue());
    }
}