package com.example.geartrackapi.controller.quote.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class QuoteMaterialDto {
    private UUID uuid;
    private String name;
    private BigDecimal purchasePrice;
    private BigDecimal marginPercent;
    private BigDecimal marginPln;
    private Integer quantity;
    private Boolean ignoreMinQuantity;
}