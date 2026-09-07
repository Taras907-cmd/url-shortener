package com.example.url_shortener.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You don't have access to this resource");
    }
}