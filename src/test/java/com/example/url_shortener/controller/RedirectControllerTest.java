package com.example.url_shortener.controller;

import com.example.url_shortener.security.JwtAuthenticationFilter;
import com.example.url_shortener.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
@AutoConfigureMockMvc(addFilters = false)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void redirect_returns302_withLocationHeader() throws Exception {
        when(linkService.resolveAndRegisterClick("abc1234")).thenReturn("https://www.google.com");

        mockMvc.perform(get("/r/abc1234"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.google.com"));
    }
}