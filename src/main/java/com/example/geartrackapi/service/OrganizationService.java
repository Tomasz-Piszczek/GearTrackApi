package com.example.geartrackapi.service;

import com.example.geartrackapi.dao.model.Organization;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.OrganizationRepository;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    
    @PreAuthorize("hasRole('ADMIN')")
    public Organization createOrganization(String organizationName) {
        if (organizationRepository.existsByOrganizationNameAndHiddenFalse(organizationName)) {
            throw new IllegalArgumentException("Organization with name already exists: " + organizationName);
        }
        
        Organization organization = Organization.builder()
                .organizationName(organizationName)
                .userId(SecurityUtils.getCurrentUserId())
                .build();
        
        return organizationRepository.save(organization);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public Organization updateOrganization(UUID organizationId, String newOrganizationName) {
        Organization organization = organizationRepository.findByIdAndHiddenFalse(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        
        if (!organization.getOrganizationName().equals(newOrganizationName) && 
            organizationRepository.existsByOrganizationNameAndHiddenFalse(newOrganizationName)) {
            throw new IllegalArgumentException("Organization with name already exists: " + newOrganizationName);
        }
        
        organization.setOrganizationName(newOrganizationName);
        return organizationRepository.save(organization);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findByIdAndHiddenFalse(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        
        organization.setHidden(true);
        organizationRepository.save(organization);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAllByHiddenFalse();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public Organization getOrganizationById(UUID organizationId) {
        return organizationRepository.findByIdWithUsersAndHiddenFalse(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public User assignUserToOrganization(String userEmail, UUID organizationId) {
        User user = userRepository.findByEmailAndHiddenFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));
        
        Organization organization = organizationRepository.findByIdAndHiddenFalse(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        
        user.setOrganization(organization);
        return userRepository.save(user);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public User removeUserFromOrganization(String userEmail) {
        User user = userRepository.findByEmailAndHiddenFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));
        
        user.setOrganization(null);
        return userRepository.save(user);
    }
}