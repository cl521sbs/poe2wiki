package com.poe2wiki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poe2wiki.dto.LoginRequest;
import com.poe2wiki.dto.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeAll
    void cleanupDb() {
        jdbcTemplate.execute("DELETE FROM refresh_tokens");
        jdbcTemplate.execute("DELETE FROM users");
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    @Order(1)
    void register_shouldReturnTokens() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    @Order(2)
    void register_duplicateUsername_shouldReturn400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("dup@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(3)
    void login_withCorrectCredentials_shouldReturnTokens() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    @Order(4)
    void login_withWrongPassword_shouldReturn401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(5)
    void register_shortUsername_shouldReturn400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("ab");
        req.setEmail("short@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(6)
    void register_invalidEmail_shouldReturn400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("validuser");
        req.setEmail("notanemail");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(400));
    }
}
