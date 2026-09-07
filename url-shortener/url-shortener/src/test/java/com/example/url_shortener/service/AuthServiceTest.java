package com.example.url_shortener.service;

import com.example.url_shortener.dto.LoginRequest;
import com.example.url_shortener.dto.RegisterRequest;
import com.example.url_shortener.dto.UserResponse;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.InvalidCredentialsException;
import com.example.url_shortener.exception.UsernameAlreadyExistsException;
import com.example.url_shortener.repository.UserRepository;
import com.example.url_shortener.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_createsUser_whenUsernameIsFree() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("taras");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("taras")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = authService.register(request);

        assertEquals("taras", response.getUsername());
        assertEquals(1L, response.getId());
    }

    @Test
    void register_throwsException_whenUsernameTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("taras");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("taras")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_returnsToken_whenCredentialsValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("taras");
        user.setPasswordHash("hashed");

        LoginRequest request = new LoginRequest();
        request.setUsername("taras");
        request.setPassword("Password123");

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("taras")).thenReturn("fake-jwt-token");

        var response = authService.login(request);

        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void login_throwsException_whenPasswordWrong() {
        User user = new User();
        user.setUsername("taras");
        user.setPasswordHash("hashed");

        LoginRequest request = new LoginRequest();
        request.setUsername("taras");
        request.setPassword("WrongPassword");

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }


    @Test
    void login_throwsException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("Password123");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}