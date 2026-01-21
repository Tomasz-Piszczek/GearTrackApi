package com.example.geartrackapi.service;

import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.SecurityUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username)  {
        User user = userRepository.findByEmailAndHiddenFalse(username).orElseThrow(() -> new EntityNotFoundException("User with name:" + username +"not found"));
        
        return SecurityUser.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .enabled(!user.getHidden() && user.getEmailVerified())
                .accountNonExpired(true)
                .accountNonLocked(user.getFailedLoginAttempts() < 5)
                .credentialsNonExpired(true)
                .role(user.getRole())
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .build();
    }
}