package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.dto.*;
import com.example.backend.service.AuthService;
import com.example.backend.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final String MOCK_EMAIL = "admin@vshop.com";

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody SignUpRequest dto) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto, null));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Đổi từ /me thành /profile cho khớp Frontend
    @GetMapping("/profile")
    public ResponseEntity<GetProfileResponse> getCurrentUser(Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : MOCK_EMAIL;
        return ResponseEntity.ok(authService.getUserProfile(email));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<Users>> listUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
}
