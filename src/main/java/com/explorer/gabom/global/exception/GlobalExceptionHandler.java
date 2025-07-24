package com.explorer.gabom.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.explorer.gabom.global.dto.ApiResponse;

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
}
