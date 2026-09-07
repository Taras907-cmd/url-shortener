package com.example.url_shortener.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String shortCode) {
        super("Link with code '" + shortCode + "' not found");
    }

    public LinkNotFoundException(Long id) {
        super("Link with id = " + id + " not found");
    }
}