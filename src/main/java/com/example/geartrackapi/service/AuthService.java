package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.controller.auth.dto.LoginDto;
import com.example.geartrackapi.controller.auth.dto.RegisterDto;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    public AuthResponseDto register(RegisterDto registerDto) {
        log.debug("[register] Registering user with email: {}", registerDto.getEmail());
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        user.setUserId(UUID.randomUUID());
        user.setEmailVerified(true);
        
        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getUuid());

        return AuthResponseDto.builder()
                .token(token)
                .email(savedUser.getEmail())
                .userId(savedUser.getUuid())
                .build();
    }
    
    public AuthResponseDto login(LoginDto loginDto) {
        log.debug("[login] Attempting login for email: {}", loginDto.getEmail());
        User user = userRepository.findByEmail(loginDto.getEmail());
        
        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            String token = jwtUtils.generateToken(user.getEmail(), user.getUuid());

            return AuthResponseDto.builder()
                    .token(token)
                    .email(user.getEmail())
                    .userId(user.getUuid())
                    .build();
        }
        
        return null;
    }
    
    public AuthResponseDto handleOAuth2Success(String email) {
        log.debug("[handleOAuth2Success] Handling OAuth2 login for email: {}", email);
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUserId(UUID.randomUUID());
            user.setEmailVerified(true);
            user = userRepository.save(user);
        }
        
        String token = jwtUtils.generateToken(user.getEmail(), user.getUuid());

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .userId(user.getUuid())
                .build();
    }
}