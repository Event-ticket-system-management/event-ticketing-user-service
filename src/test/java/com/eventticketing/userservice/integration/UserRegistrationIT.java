package com.eventticketing.userservice.integration;

import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserRegistrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test: Should register user and save hashed password in real database")
    void registerUser_EndToEnd_Success() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .password("Thamindu@1234")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("thamindu@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.id").exists());

        Optional<User> savedUserOptional = userRepository.findByEmail("thamindu@gmail.com");
        assertThat(savedUserOptional).isPresent();

        User savedUser = savedUserOptional.get();

        assertThat(savedUser.getName()).isEqualTo("Thamindu weeravaradhana");

        assertThat(savedUser.getPassword()).isNotEqualTo("Thamindu@1234");

        assertThat(passwordEncoder.matches("Thamindu@1234", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Integration Test: Should fail with 409 Conflict when attempting to register duplicate email")
    void registerUser_EndToEnd_DuplicateEmail_Conflict() throws Exception {
        User existingUser = User.builder()
                .name("Existing User")
                .email("thamindu@gmail.com")
                .password(passwordEncoder.encode("Thamindu@1234"))
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(existingUser);

        RegisterRequestDto duplicateRequest = RegisterRequestDto.builder()
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .password("Thamindu@1234")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email is already registered!"));
    }


}
