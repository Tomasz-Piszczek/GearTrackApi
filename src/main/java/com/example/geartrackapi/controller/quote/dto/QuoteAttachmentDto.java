package com.example.geartrackapi.controller.quote.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuoteAttachmentDto {
    private UUID uuid;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
