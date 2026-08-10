package com.example.geartrackapi.controller.tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ToolGroupDto {
    private UUID uuid;
    private String name;
}
