package com.explorer.gabom.domain.auth.controller;

import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Email-AuthAPI Document", description = "이메일 인증 및 코드전송 API 문서화")
public interface EmailAuthControllerDocs {

	@Operation(summary = "이메일 인증 코드 전송")
	@ApiResponse(responseCode = "200", description = "인증 코드를 이메일로 전송했습니다.")
	ResponseEntity<?> requestEmail(EmailRequest request);

	@Operation(summary = "이메일 인증 코드 검증")
	@ApiResponse(responseCode = "200", description = "이메일 인증이 완료 되었습니다.")
	ResponseEntity<?> verifiedEmail(EmailCodeVerifyRequest request);

	@Operation(summary = "비밀번호 재설정 인증코드 전송")
	@ApiResponse(responseCode = "200", description = "비밀번호 재설정 인증 코드를 이메일로 전송했습니다.")
	ResponseEntity<?> passwordResetRequestEmail(PasswordResetRequest request);

	@Operation(summary = "비밀번호 재설정")
	@ApiResponse(responseCode = "200", description = "이메일 인증이 완료 되었습니다.")
	ResponseEntity<?> passwordResetVerifiedEmail(PasswordResetVerifyRequest resetVerifyRequest);

	}
