package com.example.geartrackapi.controller.quote.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class QuoteProductionActivityDto {
    private UUID uuid;
    private String name;
    private Integer workTimeHours;
    private Integer workTimeMinutes;
    private BigDecimal price;
    private BigDecimal marginPercent;
    private BigDecimal marginPln;
    private Boolean ignoreMinQuantity;
}