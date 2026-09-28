package com.eventticketing.userservice.service;

import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.exception.EmailAlreadyExistsException;
import com.eventticketing.userservice.repository.UserRepository;
import com.eventticketing.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequestDto request;
    private User user;


    @BeforeEach
    void setup(){
      request = RegisterRequestDto.builder()
              .name("Thamindu weeravaradhana")
              .email("thamindu@gmail.com")
              .password("Thamindu@1234")
              .build();

      user = User.builder()
              .id(UUID.randomUUID())
              .name("Thamindu weeravaradhana")
              .email("thamindu@gmail.com")
              .password("hashed_password_xyz")
              .role(UserRole.ROLE_USER)
              .createdAt(OffsetDateTime.now())
              .build();
    }


    @Test
    @DisplayName("Should successfully register a new user when email is unique")
    void registerUser_Success(){
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password_xyz");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto response = userService.registerUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("thamindu@gmail.com");
        assertThat(response.getRole()).isEqualTo(UserRole.ROLE_USER);

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("Thamindu@1234");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void registerUser_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email is already registered!");

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

}






































