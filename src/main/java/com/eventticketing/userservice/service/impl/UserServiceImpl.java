package com.eventticketing.userservice.service.impl;

import com.eventticketing.userservice.dto.request.LoginRequestDto;
import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.AuthResponseDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.enums.UserRole;
import com.eventticketing.userservice.exception.EmailAlreadyExistsException;
import com.eventticketing.userservice.repository.UserRepository;
import com.eventticketing.userservice.security.JwtProvider;
import com.eventticketing.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public UserResponseDto registerUser(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email is already registered!");
        }

        User newUser = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(UserRole.ROLE_USER))
                .build();

        User savedUser = userRepository.save(newUser);

         return UserResponseDto.builder()
                 .id(savedUser.getId())
                 .name(savedUser.getUsername())
                 .email(savedUser.getEmail())
                 .role(savedUser.getRoles())
                 .createdAt(savedUser.getCreatedAt())
                 .build();


    }

    @Override
    public AuthResponseDto loginUser(LoginRequestDto request) {

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );

            Authentication authenticate = authenticationManager.authenticate(authentication);

            String jwtToken = jwtProvider.generateToken(authenticate);

            return AuthResponseDto.builder()
                    .token(jwtToken)
                    .tokenType("Bearer")
                    .build();


    }
}














