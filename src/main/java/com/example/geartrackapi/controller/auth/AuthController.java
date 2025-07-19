package com.example.geartrackapi.controller.auth;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.controller.auth.dto.LoginDto;
import com.example.geartrackapi.controller.auth.dto.RegisterDto;
import com.example.geartrackapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterDto registerDto) {
        log.info("[register] User registration attempt for email: {}", registerDto.getEmail());
        return ResponseEntity.ok(authService.register(registerDto));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        log.info("[login] User login attempt for email: {}", loginDto.getEmail());
        AuthResponseDto response = authService.login(loginDto);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLogin(@RequestBody GoogleTokenDto googleTokenDto) {
        log.info("[googleLogin] Google OAuth2 login attempt");
        
        try {
            AuthResponseDto authResponse = authService.handleGoogleLogin(googleTokenDto.getIdToken());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("[googleLogin] Google login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    public static class GoogleTokenDto {
        private String idToken;
        
        public String getIdToken() {
            return idToken;
        }
        
        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}