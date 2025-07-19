package com.example.geartrackapi.controller.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {
    private String email;
    private String password;
}