package com.example.url_shortener.exception;

public class InvalidExpirationException extends RuntimeException {
    public InvalidExpirationException() {
        super("expiresAt must be in the future");
    }
}