package com.example.url_shortener.controller;

import com.example.url_shortener.dto.AuthResponse;
import com.example.url_shortener.dto.LoginRequest;
import com.example.url_shortener.dto.RegisterRequest;
import com.example.url_shortener.dto.UserResponse;
import com.example.url_shortener.security.JwtAuthenticationFilter;
import com.example.url_shortener.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_returns201() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("taras");
        response.setCreatedAt(LocalDateTime.now());

        when(authService.register(any())).thenReturn(response);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("taras");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_returns200() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("fake-token"));

        LoginRequest request = new LoginRequest();
        request.setUsername("taras");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}