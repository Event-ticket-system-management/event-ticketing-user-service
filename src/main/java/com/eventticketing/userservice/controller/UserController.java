package com.eventticketing.userservice.controller;

import com.eventticketing.userservice.dto.request.LoginRequestDto;
import com.eventticketing.userservice.dto.request.RegisterRequestDto;
import com.eventticketing.userservice.dto.response.AuthResponseDto;
import com.eventticketing.userservice.dto.response.UserResponseDto;
import com.eventticketing.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/visitor/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto request){
        UserResponseDto response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/visitor/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto request){
        AuthResponseDto response = userService.loginUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
