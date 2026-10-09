
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

    private static final String LOGIN_URL =
            "/api/v1/user/visitor/login";

    private static final String TEST_EMAIL =
            "thamindu@gmail.com";

    private static final String TEST_PASSWORD =
            "Password123";

    private User savedUser;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        User user = User.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(List.of(UserRole.ROLE_USER))
                .username(TEST_EMAIL)
                .build();

        savedUser = userRepository.saveAndFlush(user);
    }

    @Test
    @DisplayName("Valid credentials should return a valid JWT with correct user ID")
    void login_EndToEnd_Success() throws Exception {

        LoginRequestDto request =
                new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

        String responseBody = mockMvc.perform(
                        post(LOGIN_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper
                .readTree(responseBody)
                .get("token")
                .asText();

        assertThat(jwtProvider.validationToken(token))
                .isTrue();

        assertThat(jwtProvider.extractUsername(token))
                .isEqualTo(savedUser.getId().toString());
    }

    @Test
    @DisplayName("Incorrect password should return 401 Unauthorized")
    void login_EndToEnd_InvalidPassword_Returns401() throws Exception {

        LoginRequestDto request =
                new LoginRequestDto(TEST_EMAIL, "WrongPassword");

        mockMvc.perform(
                        post(LOGIN_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Non-existent email should return 401 Unauthorized")
    void login_EndToEnd_NonExistentUser_Returns401() throws Exception {

        LoginRequestDto request =
                new LoginRequestDto(
                        "nonexistent@example.com",
                        TEST_PASSWORD
                );

        mockMvc.perform(
                        post(LOGIN_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }
}
