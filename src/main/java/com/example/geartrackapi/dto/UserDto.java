package com.example.geartrackapi.dto;

import com.example.geartrackapi.dao.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID userId;
    private String email;
    private Role role;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrganizationSummaryDto organization;
    
    @Data
    @Builder
    public static class OrganizationSummaryDto {
        private UUID id;
        private String organizationName;
    }
}