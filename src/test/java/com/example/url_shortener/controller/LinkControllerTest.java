package com.example.url_shortener.controller;

import com.example.url_shortener.dto.CreateLinkRequest;
import com.example.url_shortener.dto.LinkResponse;
import com.example.url_shortener.dto.UpdateLinkRequest;
import com.example.url_shortener.security.JwtAuthenticationFilter;
import com.example.url_shortener.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkController.class)
@AutoConfigureMockMvc(addFilters = false) // Вимикаємо фільтри безпеки для Unit-тесту
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Повертаємо, щоб контекст завантажувався

    @Autowired
    private ObjectMapper objectMapper;

    // Створюємо Principal для передачі в mockMvc
    private final Principal mockPrincipal = new UsernamePasswordAuthenticationToken("taras", null);

    private LinkResponse sampleResponse() {
        LinkResponse response = new LinkResponse();
        response.setId(1L);
        response.setShortUrl("http://localhost:8080/r/abc1234");
        response.setOriginalUrl("https://www.google.com");
        response.setCreatedAt(LocalDateTime.now());
        response.setExpiresAt(LocalDateTime.now().plusDays(30));
        response.setClickCount(0L);
        response.setOwnerUsername("taras");
        return response;
    }

    @Test
    void create_returns201() throws Exception {
        when(linkService.create(any(CreateLinkRequest.class), anyString())).thenReturn(sampleResponse());

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://www.google.com");

        mockMvc.perform(post("/api/v1/links")
                        .principal(mockPrincipal) // Явно передаємо Principal
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void listMyLinks_returns200() throws Exception {
        when(linkService.listMyLinks(anyString())).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/links")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());
    }

    @Test
    void listMyActiveLinks_returns200() throws Exception {
        when(linkService.listMyActiveLinks(anyString())).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/links/active")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());
    }

    @Test
    void update_returns200() throws Exception {
        when(linkService.update(eq(1L), any(UpdateLinkRequest.class), anyString())).thenReturn(sampleResponse());

        UpdateLinkRequest request = new UpdateLinkRequest();
        request.setOriginalUrl("https://www.example.com");

        mockMvc.perform(patch("/api/v1/links/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/links/1")
                        .principal(mockPrincipal))
                .andExpect(status().isNoContent());
    }
}