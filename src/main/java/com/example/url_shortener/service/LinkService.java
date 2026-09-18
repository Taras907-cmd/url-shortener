package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateLinkRequest;
import com.example.url_shortener.dto.LinkResponse;
import com.example.url_shortener.dto.UpdateLinkRequest;
import com.example.url_shortener.entity.Link;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.AccessDeniedException;
import com.example.url_shortener.exception.InvalidExpirationException;
import com.example.url_shortener.exception.LinkNotFoundException;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.repository.LinkRepository;
import com.example.url_shortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
    private static final int MAX_CREATE_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final String baseUrl;

    public LinkService(LinkRepository linkRepository,
                       UserRepository userRepository,
                       @Value("${app.base-url}") String baseUrl) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.baseUrl = baseUrl;
    }

    public LinkResponse create(CreateLinkRequest request, String username) {
        User user = getUser(username);

        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));
        link.setUser(user);

        int attempts = 0;
        while (true) {
            link.setShortCode(generateRandomCode());
            try {
                Link saved = linkRepository.save(link);
                return toResponse(saved);
            } catch (DataIntegrityViolationException ex) {
                if (++attempts >= 5) {
                    throw ex;
                }
            }
        }
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
            if (request.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new InvalidExpirationException();
            }
            link.setExpiresAt(request.getExpiresAt());
        }

        return toResponse(link);
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
        response.setShortUrl(baseUrl + "/r/" + link.getShortCode());
        response.setOriginalUrl(link.getOriginalUrl());
        response.setCreatedAt(link.getCreatedAt());
        response.setExpiresAt(link.getExpiresAt());
        response.setClickCount(link.getClickCount());
        response.setOwnerUsername(link.getUser().getUsername());
        return response;
    }
}