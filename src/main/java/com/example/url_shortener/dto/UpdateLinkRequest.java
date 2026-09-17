package com.example.url_shortener.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateLinkRequest {

    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;

    private LocalDateTime expiresAt;
}