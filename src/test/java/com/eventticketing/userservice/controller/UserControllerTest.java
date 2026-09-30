package com.eventticketing.userservice.controller;

import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.exception.EmailAlreadyExistsException;
import com.eventticketing.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/user/register - Should return 201 Created when request body is valid")
    void register_Success() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .password("Thamindu@1234")
                .build();

        UserResponseDto response = UserResponseDto.builder()
                .id(UUID.randomUUID())
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .role(UserRole.ROLE_USER)
                .createdAt(OffsetDateTime.now())
                .build();

        when(userService.registerUser(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("thamindu@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/v1/user/register - Should return 400 Bad Request when request validation fails")
    void register_ValidationError() throws Exception {
        RegisterRequestDto invalidRequest = RegisterRequestDto.builder()
                .name("")
                .email("invalid-email")
                .password("123")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/user/register - Should return 409 Conflict when email already exists")
    void register_EmailConflict() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .password("Thamindu@1234")
                .build();

        when(userService.registerUser(any(RegisterRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email is already registered!"));

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("Email is already registered!")));
    }

}