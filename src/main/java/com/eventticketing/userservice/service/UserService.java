package com.eventticketing.userservice.service;

import com.eventticketing.userservice.dto.request.LoginRequestDto;
import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.AuthResponseDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;

public interface UserService {
    public UserResponseDto registerUser(RegisterRequestDto request);
    public AuthResponseDto loginUser(LoginRequestDto request);
}
