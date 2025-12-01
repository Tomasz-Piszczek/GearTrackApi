package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.controller.auth.dto.LoginDto;
import com.example.geartrackapi.controller.auth.dto.RegisterDto;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.google.client-id}")
    private String googleClientId;
    
    public AuthResponseDto register(RegisterDto registerDto) {
        log.debug("[register] Registering user with email: {}", registerDto.getEmail());
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        user.setUserId(UUID.randomUUID());
        user.setEmailVerified(true);
        
        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = jwtUtils.generateRefreshToken(savedUser.getEmail(), savedUser.getId());

        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .userId(savedUser.getId())
                .build();
    }
    
    public AuthResponseDto login(LoginDto loginDto) {
        log.debug("[login] Attempting login for email: {}", loginDto.getEmail());
        User user = userRepository.findByEmail(loginDto.getEmail());
        
        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            String token = jwtUtils.generateToken(user.getEmail(), user.getId());
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId());

            return AuthResponseDto.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .userId(user.getId())
                    .build();
        }
        
        return null;
    }
    
    public AuthResponseDto handleGoogleLogin(String idToken) {
        log.debug("[handleGoogleLogin] Verifying Google ID token");
        
        try {
            // Verify the Google ID token
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode tokenInfo = objectMapper.readTree(response.getBody());
                
                // Verify the token is for our app
                String audience = tokenInfo.get("aud").asText();
                if (!googleClientId.equals(audience)) {
                    throw new RuntimeException("Invalid token audience");
                }
                
                // Extract user information
                String email = tokenInfo.get("email").asText();
                String name = tokenInfo.get("name").asText();
                
                log.debug("[handleGoogleLogin] Google login for email: {}", email);
                
                // Find or create user
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    user = new User();
                    user.setEmail(email);
                    user.setUserId(UUID.randomUUID());
                    user.setEmailVerified(true);
                    user.setPasswordHash("GOOGLE_OAUTH2_USER");
                    user = userRepository.save(user);
                    log.debug("[handleGoogleLogin] Created new user for email: {}", email);
                }
                
                String token = jwtUtils.generateToken(user.getEmail(), user.getId());
                String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId());

                return AuthResponseDto.builder()
                        .token(token)
                        .refreshToken(refreshToken)
                        .email(user.getEmail())
                        .userId(user.getId())
                        .build();
            } else {
                throw new RuntimeException("Invalid Google ID token");
            }
        } catch (Exception e) {
            log.error("[handleGoogleLogin] Error verifying Google token: {}", e.getMessage());
            throw new RuntimeException("Invalid Google ID token", e);
        }
    }
    
    public AuthResponseDto refreshToken(String refreshToken) {
        log.debug("[refreshToken] Refreshing token");
        
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String email = jwtUtils.getUsernameFromToken(refreshToken);
        UUID userId = jwtUtils.getUserIdFromToken(refreshToken);
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        String newToken = jwtUtils.generateToken(email, userId);
        String newRefreshToken = jwtUtils.generateRefreshToken(email, userId);
        
        return AuthResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .email(email)
                .userId(userId)
                .build();
    }
}