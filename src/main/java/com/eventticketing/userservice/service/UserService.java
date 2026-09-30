package com.eventticketing.userservice.service;

import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;

public interface UserService {
    public UserResponseDto registerUser(RegisterRequestDto request);
}
