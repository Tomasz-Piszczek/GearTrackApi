package com.example.geartrackapi.controller.tool.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolQuantityDto {
    private int availableQuantity;
    private int totalAssigned;
}