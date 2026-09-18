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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        linkService = new LinkService(linkRepository, userRepository, "http://localhost:8080");
    }

    @Test
    void create_savesLinkWithGeneratedCode() {
        User user = new User();
        user.setId(1L);
        user.setUsername("taras");

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://www.google.com");

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> {
            Link l = invocation.getArgument(0);
            l.setId(1L);
            return l;
        });

        LinkResponse response = linkService.create(request, "taras");

        assertEquals("https://www.google.com", response.getOriginalUrl());
        assertNotNull(response.getShortUrl());
        assertEquals("taras", response.getOwnerUsername());
        assertEquals(0L, response.getClickCount());
    }

    @Test
    void create_retriesGeneration_whenCodeCollisionOccurs() {
        User user = new User();
        user.setId(1L);

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://www.google.com");

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"))
                .thenAnswer(i -> i.getArgument(0));

        LinkResponse response = linkService.create(request, "taras");

        assertNotNull(response);
        verify(linkRepository, times(2)).save(any(Link.class));
    }

    @Test
    void create_throwsUserNotFoundException_whenUserDoesNotExist() {
        CreateLinkRequest request = new CreateLinkRequest();
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> linkService.create(request, "unknown"));
    }

    @Test
    void listMyLinks_returnsListOfLinks() {
        User user = new User();
        user.setId(1L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(user);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findByUserId(1L)).thenReturn(List.of(link));

        List<LinkResponse> responses = linkService.listMyLinks("taras");

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void listMyActiveLinks_returnsActiveLinks() {
        User user = new User();
        user.setId(1L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(user);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findByUserIdAndExpiresAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(link));

        List<LinkResponse> responses = linkService.listMyActiveLinks("taras");

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void update_updatesFields_whenOwner() {
        User user = new User();
        user.setId(1L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(user);
        link.setOriginalUrl("https://old.com");

        UpdateLinkRequest request = new UpdateLinkRequest();
        request.setOriginalUrl("https://new.com");
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(10);
        request.setExpiresAt(newExpiresAt);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link));

        LinkResponse response = linkService.update(10L, request, "taras");

        assertEquals("https://new.com", response.getOriginalUrl());
        assertEquals(newExpiresAt, response.getExpiresAt());
    }

    @Test
    void update_throwsAccessDenied_whenNotOwner() {
        User owner = new User();
        owner.setId(1L);

        User intruder = new User();
        intruder.setId(2L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(owner);

        when(userRepository.findByUsername("intruder")).thenReturn(Optional.of(intruder));
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link));

        UpdateLinkRequest request = new UpdateLinkRequest();

        assertThrows(AccessDeniedException.class, () -> linkService.update(10L, request, "intruder"));
    }

    @Test
    void update_throwsNotFound_whenLinkDoesNotExist() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateLinkRequest request = new UpdateLinkRequest();

        assertThrows(LinkNotFoundException.class, () -> linkService.update(99L, request, "taras"));
    }

    @Test
    void delete_deletesLink_whenOwner() {
        User user = new User();
        user.setId(1L);

        Link link = new Link();
        link.setId(10L);
        link.setUser(user);

        when(userRepository.findByUsername("taras")).thenReturn(Optional.of(user));
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link));

        linkService.delete(10L, "taras");

        verify(linkRepository).delete(link);
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
        verify(linkRepository).incrementClickCount(1L);
    }

    @Test
    void resolveAndRegisterClick_throwsNotFound_whenExpired() {
        Link link = new Link();
        link.setShortCode("expired1");
        link.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(linkRepository.findByShortCode("expired1")).thenReturn(Optional.of(link));

        assertThrows(LinkNotFoundException.class, () -> linkService.resolveAndRegisterClick("expired1"));
    }

    @Test
    void resolveAndRegisterClick_throwsNotFound_whenCodeDoesNotExist() {
        when(linkRepository.findByShortCode("unknown")).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.resolveAndRegisterClick("unknown"));
    }
}