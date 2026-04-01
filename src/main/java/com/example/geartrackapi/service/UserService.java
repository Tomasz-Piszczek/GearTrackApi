package com.example.geartrackapi.service;

import com.example.geartrackapi.dao.model.Role;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.dto.UserDto;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        log.info("[getCurrentUser] Fetching user data for email: {}", email);
        User user = userRepository.findByEmailAndHiddenFalse(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersByOrganization() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        log.info("[getAllUsersByOrganization] Fetching users for organization: {}", organizationId);
        return userRepository.findByOrganizationIdAndHiddenFalse(organizationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId) {
        log.info("[getUserById] Fetching user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }
    
    @Transactional
    public UserDto updateUserRole(UUID userId, Role role) {
        log.info("[updateUserRole] Updating user {} role to {}", userId, role);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        user = userRepository.save(user);
        return convertToDto(user);
    }
    
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("[deleteUser] Deleting user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(user);
    }
    
    private UserDto convertToDto(User user) {
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