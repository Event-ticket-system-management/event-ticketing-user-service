package com.eventticketing.userservice.controller;

import com.eventticketing.userservice.dto.request.LoginRequestDto;
import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.AuthResponseDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.exception.BadCredentialsException;
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
import java.util.Collections;
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
    @DisplayName("POST /api/v1/user/visitor/register - Should return 201 Created when request body is valid")
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
                .role(Collections.singletonList(UserRole.ROLE_USER))
                .createdAt(OffsetDateTime.now())
                .build();

        when(userService.registerUser(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/visitor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("thamindu@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/v1/user/visitor/register - Should return 400 Bad Request when request validation fails")
    void register_ValidationError() throws Exception {
        RegisterRequestDto invalidRequest = RegisterRequestDto.builder()
                .name("")
                .email("invalid-email")
                .password("123")
                .build();

        mockMvc.perform(post("/api/v1/user/visitor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/user/visitor/register - Should return 409 Conflict when email already exists")
    void register_EmailConflict() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .name("Thamindu weeravaradhana")
                .email("thamindu@gmail.com")
                .password("Thamindu@1234")
                .build();

        when(userService.registerUser(any(RegisterRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email is already registered!"));

        mockMvc.perform(post("/api/v1/user/visitor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("Email is already registered!")));
    }

    @Test
    @DisplayName("Should return 200 OK and AuthResponseDto on valid login credentials")
    void login_Success() throws Exception {
        LoginRequestDto request = new LoginRequestDto("thamindu@gmail.com", "Thamindu@1234");
        AuthResponseDto response = AuthResponseDto.builder()
                .token("jwt_token_xyz")
                .build();

        when(userService.loginUser(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/visitor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token_xyz"));

    }

    @Test
    @DisplayName("Should return 401 Unauthorized when BadCredentialsException is thrown")
    void login_InvalidCredentials_Returns401() throws Exception {
        LoginRequestDto request = new LoginRequestDto("thamindu@gmail1.com", "Thamindu@12341");

        when(userService.loginUser(any(LoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/v1/user/visitor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request payload is invalid")
    void login_InvalidPayload_Returns400() throws Exception {
        LoginRequestDto invalidRequest = new LoginRequestDto("not-an-email", "");

        mockMvc.perform(post("/api/v1/user/visitor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}