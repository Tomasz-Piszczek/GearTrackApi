package com.example.geartrackapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignUserRequest {
    private String userEmail;
    private UUID organizationId;
}