package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.tool.dto.ToolGroupDto;
import com.example.geartrackapi.dao.model.ToolGroup;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class ToolGroupMapper {

    public ToolGroupDto toDto(ToolGroup toolGroup) {
        return ToolGroupDto.builder()
                .uuid(toolGroup.getId())
                .name(toolGroup.getName())
                .build();
    }

    public ToolGroup toEntity(ToolGroupDto dto) {
        return ToolGroup.builder()
                .name(dto.getName())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }

    public void updateEntity(ToolGroup existing, ToolGroupDto dto) {
        existing.setName(dto.getName());
    }
}
