package com.example.geartrackapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrganizationDto {
    private UUID id;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UserDto> users;
}