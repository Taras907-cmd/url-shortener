package com.example.url_shortener.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-must-be-at-least-32-bytes-long");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generateToken_andExtractUsername_roundTrip() {
        String token = jwtService.generateToken("taras");
        assertEquals("taras", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_returnsTrue_forCorrectUsername() {
        String token = jwtService.generateToken("taras");
        assertTrue(jwtService.isTokenValid(token, "taras"));
    }

    @Test
    void isTokenValid_returnsFalse_forWrongUsername() {
        String token = jwtService.generateToken("taras");
        assertFalse(jwtService.isTokenValid(token, "someoneElse"));
    }
}