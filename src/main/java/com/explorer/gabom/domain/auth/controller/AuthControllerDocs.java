package com.explorer.gabom.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "AuthAPI",
	description = "유저 및 관리자 회원가입, 로그인, 닉네임 중복 검사 등 인증(Auth) 관련 기능을 제공합니다."
)
public interface AuthControllerDocs {

	// 회원가입
	@Operation(
		summary     = "회원가입",
		description = "신규 유저 또는 관리자 계정을 생성합니다.  \n"
			+ "- 이메일 인증 및 닉네임 중복 검사를 수행하고 비밀번호는 안전하게 해시 처리하여 저장합니다.  \n"
			+ "- 요청이 성공하면 HTTP 201 상태 코드를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 이메일로 가입 시도."),
		@ApiResponse(responseCode = "409", description = "이미 등록된 이메일로 가입 시도"),
		@ApiResponse(responseCode = "409", description = "이미 등록된 닉네임으로 가입 시도")
	})
	ResponseEntity<?> signup(
		@RequestBody SignupRequest requestDto
	);

	// 테스트용 회원가입
	@Operation(
		summary     = "회원가입(테스트용)",
		description = "개발/테스트 환경에서 빠르게 회원을 생성하기 위한 엔드포인트입니다.  \n"
			+ "- 이메일 인증 검사를 생략합니다.  \n"
			+ "- 실제 운영 환경에서는 사용을 지양합니다.  \n"
			+ "- 내부 테스트 데이터를 위해 사용되며, HTTP 201을 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "409", description = "이미 등록된 이메일로 가입 시도"),
		@ApiResponse(responseCode = "409", description = "이미 등록된 닉네임으로 가입 시도")
	})
	ResponseEntity<?> testSignup(
		@RequestBody SignupRequest requestDto
	);

	// 로그인
	@Operation(
		summary     = "로그인",
		description = "등록된 계정의 이메일과 비밀번호를 검증하여 인증을 수행합니다.  \n"
			+ "- 검증 성공 시 JWT 액세스/리프레시 토큰을 발급합니다.  \n"
			+ "- 잘못된 자격 증명이면 HTTP 401 또는 403을 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급 완료"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저 정보"),
		@ApiResponse(responseCode = "403", description = "비밀번호가 불일치")
	})
	ResponseEntity<?> login(
		@RequestBody LoginRequest request
	);

	// 닉네임 중복 확인
	@Operation(
		summary     = "닉네임 중복 확인",
		description = "회원가입 시 입력된 닉네임이 이미 사용 중인지 확인합니다.  \n"
			+ "- 사용 가능 시 HTTP 200, 중복 시 HTTP 409을 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "닉네임 사용 가능"),
		@ApiResponse(responseCode = "409", description = "이미 등록된 닉네임")
	})
	@Parameter(
		name        = "nickname",
		description = "중복 검사를 수행할 닉네임",
		required    = true
	)
	ResponseEntity<?> checkNickname(
		@RequestParam("nickname") String nickname
	);
}
