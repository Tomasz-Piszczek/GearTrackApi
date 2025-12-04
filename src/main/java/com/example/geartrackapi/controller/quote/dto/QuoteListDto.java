package com.example.geartrackapi.controller.quote.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuoteListDto {
    private UUID uuid;
    private String documentNumber;
    private String contractorCode;
    private String contractorName;
    private String productCode;
    private String productName;
    private Integer minQuantity;
    private Integer totalQuantity;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}