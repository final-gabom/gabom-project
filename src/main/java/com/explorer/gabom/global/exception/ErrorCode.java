package com.explorer.gabom.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "왜 터졌지?"),

	//Title
	TITLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 칭호입니다."),
	TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 칭호를 찾을 수 없습니다."),

	// Valid
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error"),

	// User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

	// Auth
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 토큰 값입니다."),
	EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 비어 있습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
	UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
	SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패했습니다."),

	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
	NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 닉네임입니다."),
	INCORRECT_PASSWORD(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

	// File
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

	// Place
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),
	PLACE_NO_PERMISSION(HttpStatus.FORBIDDEN, "해당 장소에 대한 권한이 없습니다."),
	NO_FIELDS_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 값이 없습니다."),
  
	// Quest
	QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 퀘스트를 찾을 수 없습니다."),

	// MissionProof
	INVALID_POOF_TYPE(HttpStatus.BAD_REQUEST, "잘못된 값입니다.");

	private final HttpStatus httpStatus;
	private final String message;

}
