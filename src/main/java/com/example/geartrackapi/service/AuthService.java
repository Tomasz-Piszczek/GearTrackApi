package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.google.client-id}")
    private String googleClientId;

    @Transactional
    public AuthResponseDto handleGoogleLogin(String idToken) {
        
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode tokenInfo = objectMapper.readTree(response.getBody());

                String audience = tokenInfo.get("aud").asText();
                if (!googleClientId.equals(audience)) {
                    throw new RuntimeException("Invalid token audience");
                }

                if (tokenInfo.has("exp")) {
                    long expirationTime = tokenInfo.get("exp").asLong();
                    long currentTime = System.currentTimeMillis() / 1000;
                    if (currentTime >= expirationTime) {
                        throw new RuntimeException("Token has expired");
                    }
                }

                if (tokenInfo.has("iss")) {
                    String issuer = tokenInfo.get("iss").asText();
                    if (!issuer.equals("https://accounts.google.com") && !issuer.equals("accounts.google.com")) {
                        throw new RuntimeException("Invalid token issuer");
                    }
                }

                String email = tokenInfo.get("email").asText();
                String name = tokenInfo.get("name").asText();


                User user = userRepository.findByEmailAndHiddenFalse(email)
                        .orElse(null);

                if (user == null) {
                    user = User.builder()
                            .email(email)
                            .emailVerified(true)
                            .build();
                    user = userRepository.save(user);
                }

                UUID organizationId = user.getOrganization() != null ? user.getOrganization().getId() : null;
                String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole(), organizationId);
                String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId(), user.getRole(), organizationId);

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
    
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtils.getUsernameFromToken(refreshToken);
        UUID userId = jwtUtils.getUserIdFromToken(refreshToken);

        User user = userRepository.findByEmailAndHiddenFalse(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID organizationId = user.getOrganization() != null ? user.getOrganization().getId() : null;
        String newToken = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole(), organizationId);
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId(), user.getRole(), organizationId);

        return AuthResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }
}