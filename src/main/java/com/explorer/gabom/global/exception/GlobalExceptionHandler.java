package com.explorer.gabom.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.explorer.gabom.global.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ApiResponse<Void>> handleBusinessException(CustomException e) {
		return ResponseEntity
			.status(e.getErrorCode().getHttpStatus()) // 예외에 정의된 상태 코드로 응답
			.body(ApiResponse.fail(e.getErrorCode())); // 공통 응답 포맷으로 메시지 반환
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.fail(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
								   ErrorCode.VALIDATION_ERROR));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ApiResponse<Void>> handleUnhandledException(Exception e) {
		log.error("[INTERNAL_SERVER_ERROR] 예기치 못한 서버 오류", e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(errorCode));
	}
}
