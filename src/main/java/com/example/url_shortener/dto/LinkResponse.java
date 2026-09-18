package com.example.url_shortener.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkResponse {
    private Long id;
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long clickCount;
    private String ownerUsername;
}