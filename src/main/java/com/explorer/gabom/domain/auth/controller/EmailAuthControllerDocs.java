package com.explorer.gabom.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Email-AuthAPI", description = "이메일 인증 및 비밀번호 재설정, 인증 코드 전송 등 이메일 인증(Auth) 관련 기능을 제공합니다.")
public interface EmailAuthControllerDocs {

	@Operation(summary = "이메일 인증 코드 전송",
		description = "이메일 인증 코드를 전송합니다. \n"
		+ "- 이메일 인증 및 인증코드 전송을 수행합니다."
		+ "- 요청이 성공하면 HTTP 200 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
		@ApiResponse(responseCode = "400", description = "이미 등록된 이메일로 가입 시도"),
		@ApiResponse(responseCode = "400", description = "이메일 형식 오류"),
		@ApiResponse(responseCode = "500", description = "이메일 전송 실패")
	})
	ResponseEntity<?> requestEmail(
		@Parameter(description = "이메일 인증 코드 요청 정보", required = true)
		@RequestBody EmailRequest request
	);

	@Operation(summary = "이메일 인증 코드 검증",
		description = "이메일 인증 코드를 검증합니다. \n"
		+ "- 검증 성공 시 HTTP 200 상태 코드를 반환합니다."
		+ "- 검증 실패 시 400 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
		@ApiResponse(responseCode = "400", description = "인증 코드 만료"),
		@ApiResponse(responseCode = "400", description = "이메일과 매칭되지 않는 인증코드"),
		@ApiResponse(responseCode = "400", description = "이미 인증된 이메일"),
		@ApiResponse(responseCode = "400", description = "인증요청이 없는 이메일")
	})
	ResponseEntity<?> verifiedEmail(
		@Parameter(description = "이메일 인증 코드 검증 요청 정보", required = true)
		@RequestBody EmailCodeVerifyRequest request
	);

	@Operation(summary = "비밀번호 재설정 인증코드 전송",
		description = "비밀번호 재설정 인증코드를 전송합니다. \n"
		+ "- 전송 성공 시 HTTP 200 상태 코드를 반환합니다."
		+ "- 전송 실패 시 404, 500 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "인증코드 전송 성공"),
		@ApiResponse(responseCode = "404", description = "해당 이메일로 가입된 유저 없음"),
		@ApiResponse(responseCode = "500", description = "이메일 전송 실패")
	})
	ResponseEntity<?> passwordResetRequestEmail(
		@Parameter(description = "비밀번호 재설정 인증 코드 검증 요청 정보", required = true)
		@RequestBody PasswordResetRequest request);

	@Operation(summary = "비밀번호 재설정",
		description = "비밀번호를 재설정합니다. \n"
			+ "- 이메일 인증 및 비밀번호 재설정을 수행합니다."
			+ "- 재설정 성공시 HTTP 200 상태코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
		@ApiResponse(responseCode = "400", description = "인증 코드 만료"),
		@ApiResponse(responseCode = "400", description = "이메일과 매칭되지 않는 인증코드"),
		@ApiResponse(responseCode = "400", description = "부적합한 비밀번호"),
		@ApiResponse(responseCode = "400", description = "해당 이메일의 사용자 없음")

	})
	ResponseEntity<?> passwordResetVerifiedEmail(
		@Parameter(description = "비밀번호 재설정 요청 정보", required = true)
		@RequestBody PasswordResetVerifyRequest resetVerifyRequest);

	}
