package com.example.geartrackapi.service;

import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        return SecurityUser.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .enabled(!user.getHidden() && user.getEmailVerified())
                .accountNonExpired(true)
                .accountNonLocked(user.getFailedLoginAttempts() < 5)
                .credentialsNonExpired(true)
                .build();
    }
}