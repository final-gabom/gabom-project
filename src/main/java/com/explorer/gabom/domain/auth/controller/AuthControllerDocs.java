package com.explorer.gabom.domain.auth.controller;

import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AuthAPI Document", description = "회원가입 및 로그인 API 문서화")
public interface AuthControllerDocs {

	@Operation(summary = "유저 및 관리자 회원가입")
	@ApiResponse(responseCode = "201", description = "회원가입을 성공했습니다.")
	ResponseEntity<?> signup(SignupRequest requestDto);

	@Operation(summary = "유저 및 관리자 회원가입(테스트용)")
	@ApiResponse(responseCode = "201", description = "회원가입을 성공했습니다.")
	ResponseEntity<?> testSignup(SignupRequest requestDto);

	@Operation(summary = "유저 및 관리자 로그인")
	@ApiResponse(responseCode = "200", description = "로그인을 성공했습니다.")
	ResponseEntity<?> login(LoginRequest request);

	@Operation(summary = "회원가입 시 닉네임 중복 확인")
	@ApiResponse(responseCode = "200", description = "닉네임 중복확인을 완료하였습니다.")
	@Parameter(name = "nickname", description = "중복 확인할 닉네임")
	ResponseEntity<?> checkNickname(String nickname);
}
