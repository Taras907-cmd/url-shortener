package com.example.url_shortener.controller;

import com.example.url_shortener.dto.AuthResponse;
import com.example.url_shortener.dto.LoginRequest;
import com.example.url_shortener.dto.RegisterRequest;
import com.example.url_shortener.dto.UserResponse;
import com.example.url_shortener.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}