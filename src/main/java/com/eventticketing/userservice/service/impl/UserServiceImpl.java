package com.eventticketing.userservice.service.impl;

import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.exception.EmailAlreadyExistsException;
import com.eventticketing.userservice.repository.UserRepository;
import com.eventticketing.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto registerUser(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email is already registered!");
        }

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);

         return UserResponseDto.builder()
                 .id(savedUser.getId())
                 .name(savedUser.getName())
                 .email(savedUser.getEmail())
                 .role(savedUser.getRole())
                 .createdAt(savedUser.getCreatedAt())
                 .build();


    }
}
