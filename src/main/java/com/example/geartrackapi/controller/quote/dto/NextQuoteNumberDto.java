package com.example.geartrackapi.controller.quote.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NextQuoteNumberDto {
    private String nextQuoteNumber;
    private Integer sequenceNumber;
    private Integer month;
    private Integer year;
}