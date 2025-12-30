package com.example.geartrackapi.controller.auth;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.controller.auth.dto.LoginDto;
import com.example.geartrackapi.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        log.info("[login] User login attempt for email: {}", loginDto.getEmail());
        return ResponseEntity.ok(authService.login(loginDto));
    }
    
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLogin(@RequestBody GoogleTokenDto googleTokenDto) {
        log.info("[googleLogin] Google OAuth2 login attempt");
        return ResponseEntity.ok(authService.handleGoogleLogin(googleTokenDto.getIdToken()));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        log.info("[refreshToken] Token refresh attempt");
        return ResponseEntity.ok(authService.refreshToken(refreshTokenDto.getRefreshToken()));
    }
    
    @Getter
    @Setter
    public static class GoogleTokenDto {
        private String idToken;
    }
    
    @Getter
    @Setter
    public static class RefreshTokenDto {
        private String refreshToken;
    }
}