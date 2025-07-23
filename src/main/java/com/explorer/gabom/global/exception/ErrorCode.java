package com.explorer.gabom.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//Title
	TITLE_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 칭호입니다."),

	TITLE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 칭호를 찾을 수 없습니다."),

	// User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

	// Auth
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 등록된 이메일입니다." ),

	NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 등록된 닉네임입니다." );

	private final HttpStatus httpStatus;
	private final String message;

}