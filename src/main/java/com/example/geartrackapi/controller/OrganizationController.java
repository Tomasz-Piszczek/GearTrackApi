package com.example.geartrackapi.controller;

import com.example.geartrackapi.dao.model.Organization;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dto.AssignUserRequest;
import com.example.geartrackapi.dto.CreateOrganizationRequest;
import com.example.geartrackapi.dto.OrganizationDto;
import com.example.geartrackapi.dto.UserDto;
import com.example.geartrackapi.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    
    private final OrganizationService organizationService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Organization> createOrganization(@RequestBody CreateOrganizationRequest request) {
        Organization organization = organizationService.createOrganization(request.getOrganizationName());
        return ResponseEntity.ok(organization);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDto> getOrganizationById(@PathVariable UUID id) {
        Organization organization = organizationService.getOrganizationById(id);
        OrganizationDto dto = convertToDto(organization);
        return ResponseEntity.ok(dto);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Organization> updateOrganization(@PathVariable UUID id, @RequestBody CreateOrganizationRequest request) {
        Organization organization = organizationService.updateOrganization(id, request.getOrganizationName());
        return ResponseEntity.ok(organization);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/assign-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> assignUserToOrganization(@RequestBody AssignUserRequest request) {
        User user = organizationService.assignUserToOrganization(request.getUserEmail(), request.getOrganizationId());
        return ResponseEntity.ok(convertUserToDto(user));
    }
    
    @PostMapping("/remove-user/{userEmail}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> removeUserFromOrganization(@PathVariable String userEmail) {
        User user = organizationService.removeUserFromOrganization(userEmail);
        return ResponseEntity.ok(convertUserToDto(user));
    }
    
    private OrganizationDto convertToDto(Organization organization) {
        List<UserDto> userDtos = organization.getUsers() != null 
            ? organization.getUsers().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList())
            : List.of();
        
        return OrganizationDto.builder()
                .id(organization.getId())
                .organizationName(organization.getOrganizationName())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .users(userDtos)
                .build();
    }
    
    private UserDto convertUserToDto(User user) {
        UserDto.OrganizationSummaryDto organizationSummary = null;
        if (user.getOrganization() != null) {
            organizationSummary = UserDto.OrganizationSummaryDto.builder()
                    .id(user.getOrganization().getId())
                    .organizationName(user.getOrganization().getOrganizationName())
                    .build();
        }
        
        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .organization(organizationSummary)
                .build();
    }
}