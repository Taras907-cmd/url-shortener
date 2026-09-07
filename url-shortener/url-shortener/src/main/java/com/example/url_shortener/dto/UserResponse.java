package com.example.url_shortener.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private Long id;
    private String username;
    private LocalDateTime createdAt;
}