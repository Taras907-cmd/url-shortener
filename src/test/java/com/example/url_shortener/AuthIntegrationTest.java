package com.example.url_shortener;

import com.example.url_shortener.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void register_returns201_onSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationUser");
        request.setPassword("Password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void register_returns409_onDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateUser");
        request.setPassword("Password123");

        restTemplate.postForEntity("/api/v1/auth/register", request, String.class);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}