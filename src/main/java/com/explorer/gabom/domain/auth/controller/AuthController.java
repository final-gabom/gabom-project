package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.SignupRequestDto;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignupRequestDto requestDto){
        ApiResponse<Void> response = authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
