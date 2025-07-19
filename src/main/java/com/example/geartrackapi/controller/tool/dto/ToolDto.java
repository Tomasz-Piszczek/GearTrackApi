package com.example.geartrackapi.controller.tool.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ToolDto {
    private UUID uuid;
    private String name;
    private String factoryNumber;
    private String size;
    private Integer quantity;
    private BigDecimal value;
}