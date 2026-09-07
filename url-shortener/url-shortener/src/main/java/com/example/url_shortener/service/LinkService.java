package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateLinkRequest;
import com.example.url_shortener.dto.LinkResponse;
import com.example.url_shortener.entity.Link;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.AccessDeniedException;
import com.example.url_shortener.exception.LinkNotFoundException;
import com.example.url_shortener.repository.LinkRepository;
import com.example.url_shortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    public LinkResponse create(CreateLinkRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));

        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setShortCode(generateUniqueCode());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));
        link.setUser(user);

        Link saved = linkRepository.save(link);
        return toResponse(saved);
    }

    public List<LinkResponse> listMyLinks(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));

        return linkRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(Long linkId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));

        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkNotFoundException(linkId));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }

        linkRepository.delete(link);
    }

    public String resolveAndRegisterClick(String shortCode) {
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkNotFoundException(shortCode);
        }

        link.setClickCount(link.getClickCount() + 1);
        linkRepository.save(link);

        return link.getOriginalUrl();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (linkRepository.existsByShortCode(code));
        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private LinkResponse toResponse(Link link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setShortCode(link.getShortCode());
        response.setOriginalUrl(link.getOriginalUrl());
        response.setCreatedAt(link.getCreatedAt());
        response.setExpiresAt(link.getExpiresAt());
        response.setClickCount(link.getClickCount());
        return response;
    }
}