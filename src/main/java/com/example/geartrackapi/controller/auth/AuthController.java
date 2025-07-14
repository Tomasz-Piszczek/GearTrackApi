package com.example.geartrackapi.controller.auth;

import com.example.geartrackapi.controller.auth.dto.AuthResponseDto;
import com.example.geartrackapi.controller.auth.dto.LoginDto;
import com.example.geartrackapi.controller.auth.dto.RegisterDto;
import com.example.geartrackapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    
    @GetMapping("/oauth2/success")
    public ResponseEntity<AuthResponseDto> oauth2Success(OAuth2AuthenticationToken authentication) {
        String email = authentication.getPrincipal().getAttribute("email");
        log.info("[oauth2Success] OAuth2 login success for email: {}", email);
        return ResponseEntity.ok(authService.handleOAuth2Success(email));
    }
}