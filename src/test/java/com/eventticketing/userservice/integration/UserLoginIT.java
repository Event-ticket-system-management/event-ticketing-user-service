package com.eventticketing.userservice.integration;

import com.eventticketing.userservice.dto.request.LoginRequestDto;
import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.repository.UserRepository;
import com.eventticketing.userservice.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970",
        "jwt.expiration=86400000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserLoginIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    private static final String LOGIN_URL = "/api/v1/user/visitor/login";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("thamindu@gmail.com")
                .password(passwordEncoder.encode("Password123"))
                .roles(List.of(UserRole.ROLE_USER))
                .username("thamindu@gmail.com")
                .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("End-to-End: Valid credentials should authenticate and return valid JWT token")
    void login_EndToEnd_Success() throws Exception {
        LoginRequestDto request = new LoginRequestDto("thamindu@gmail.com", "Password123");

        String responseBody = mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(responseBody).get("token").asText();
        assertThat(jwtProvider.validationToken(token)).isTrue();
        assertThat(jwtProvider.extractUsername(token)).isEqualTo("thamindu@gmail.com");
    }

    @Test
    @DisplayName("End-to-End: Incorrect password should return 401 Unauthorized")
    void login_EndToEnd_InvalidPassword_Returns401() throws Exception {
        LoginRequestDto request = new LoginRequestDto("thamindu@gmail.com", "WrongPassword");

        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("End-to-End: Non-existent email should return 401 Unauthorized")
    void login_EndToEnd_NonExistentUser_Returns401() throws Exception {
        LoginRequestDto request = new LoginRequestDto("nonexistent@example.com", "Password123");

        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}