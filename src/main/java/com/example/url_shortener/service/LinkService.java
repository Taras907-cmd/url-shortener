package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateLinkRequest;
import com.example.url_shortener.dto.LinkResponse;
import com.example.url_shortener.dto.UpdateLinkRequest;
import com.example.url_shortener.entity.Link;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.AccessDeniedException;
import com.example.url_shortener.exception.LinkNotFoundException;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.repository.LinkRepository;
import com.example.url_shortener.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LinkService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public LinkResponse create(CreateLinkRequest request, String username) {
        User user = getUser(username);

        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setShortCode(generateUniqueCode());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));
        link.setUser(user);

        Link saved = linkRepository.save(link);
        return toResponse(saved);
    }

    public List<LinkResponse> listMyLinks(String username) {
        User user = getUser(username);
        return linkRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LinkResponse> listMyActiveLinks(String username) {
        User user = getUser(username);
        return linkRepository.findByUserIdAndExpiresAtAfter(user.getId(), LocalDateTime.now()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LinkResponse update(Long linkId, UpdateLinkRequest request, String username) {
        User user = getUser(username);
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkNotFoundException(linkId));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }

        if (request.getOriginalUrl() != null) {
            link.setOriginalUrl(request.getOriginalUrl());
        }
        if (request.getExpiresAt() != null) {
            link.setExpiresAt(request.getExpiresAt());
        }

        return toResponse(linkRepository.save(link));
    }

    public void delete(Long linkId, String username) {
        User user = getUser(username);

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

        linkRepository.incrementClickCount(link.getId());
        return link.getOriginalUrl();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
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