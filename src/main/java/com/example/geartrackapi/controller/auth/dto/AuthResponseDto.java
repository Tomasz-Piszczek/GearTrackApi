package com.example.geartrackapi.controller.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private String email;
    private UUID userId;
}