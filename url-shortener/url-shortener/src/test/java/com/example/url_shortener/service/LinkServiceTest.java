package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateLinkRequest;
import com.example.url_shortener.dto.LinkResponse;
import com.example.url_shortener.entity.Link;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.AccessDeniedException;
import com.example.url_shortener.exception.LinkNotFoundException;
import com.example.url_shortener.repository.LinkRepository;
import com.example.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    void create_savesLinkWithGeneratedCode() {
        User user = new User();
        user.setId(1L);
        user.setUsername("taras");

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://www.google.com");

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.existsByShortCode(any())).thenReturn(false);
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> {
            Link l = invocation.getArgument(0);
            l.setId(1L);
            return l;
        });

        LinkResponse response = linkService.create(request, "taras");

        assertEquals("https://www.google.com", response.getOriginalUrl());
        assertNotNull(response.getShortCode());
        assertEquals(0L, response.getClickCount());
    }

    @Test
    void delete_throwsAccessDenied_whenNotOwner() {
        User owner = new User();
        owner.setId(1L);

        User intruder = new User();
        intruder.setId(2L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(owner);

        when(userRepository.findByUsername("intruder")).thenReturn(Optional.of(intruder));
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link));

        assertThrows(AccessDeniedException.class, () -> linkService.delete(10L, "intruder"));
    }

    @Test
    void delete_throwsNotFound_whenLinkDoesNotExist() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.delete(99L, "taras"));
    }

    @Test
    void resolveAndRegisterClick_incrementsCount() {
        Link link = new Link();
        link.setId(1L);
        link.setShortCode("abc1234");
        link.setOriginalUrl("https://www.google.com");
        link.setExpiresAt(LocalDateTime.now().plusDays(1));
        link.setClickCount(5L);

        when(linkRepository.findByShortCode("abc1234")).thenReturn(Optional.of(link));

        String url = linkService.resolveAndRegisterClick("abc1234");

        assertEquals("https://www.google.com", url);
        assertEquals(6L, link.getClickCount());
        verify(linkRepository).save(link);
    }

    @Test
    void resolveAndRegisterClick_throwsNotFound_whenExpired() {
        Link link = new Link();
        link.setShortCode("expired1");
        link.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(linkRepository.findByShortCode("expired1")).thenReturn(Optional.of(link));

        assertThrows(LinkNotFoundException.class, () -> linkService.resolveAndRegisterClick("expired1"));
    }
}