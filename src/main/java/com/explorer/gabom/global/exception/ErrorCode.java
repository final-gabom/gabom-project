package com.explorer.gabom.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "왜 터졌지?"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 경로를 찾을 수 없습니다."),
	NOT_RESOURCE_OWNER(HttpStatus.FORBIDDEN, "해당 리소스 소유자가 아닙니다."),

	//Title
	TITLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 칭호입니다."),
	TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 칭호를 찾을 수 없습니다."),

	// Valid
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error"),

	// User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
	CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자신을 차단할 수 없습니다."),
	ALREADY_BLOCKED_USER(HttpStatus.CONFLICT, "이미 차단된 유저입니다."),
	NOT_BLOCKED_USER(HttpStatus.BAD_REQUEST, "차단 목록에 존재하지 않는 유저입니다."),
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
	EXPIRED_CODE(HttpStatus.UNAUTHORIZED, "만료된 인증코드입니다."),
	CODE_NOT_MATCH(HttpStatus.FORBIDDEN, "인증코드가 일치하지 않습니다."),
	EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "이미 인증된 이메일입니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "인증되지 않은 이메일입니다."),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),

	// File
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),

	// Place
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),
	NO_FIELDS_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 값이 없습니다."),

	// Exploration
	EXPLORATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 탐험을 찾을 수 없습니다."),
	EXPLORATION_NO_PERMISSION(HttpStatus.FORBIDDEN, "해당 탐험에 대한 권한이 없습니다."),
	EXPLORATION_ALREADY_ENDED(HttpStatus.CONFLICT, "탐험이 이미 종료되었습니다."),
	ALREADY_STARTED_EXPLORATION(HttpStatus.CONFLICT, "이미 해당 장소 탐험이 진행 중입니다."),
	NO_ACTIVE_EXPLORATION(HttpStatus.NOT_FOUND, "현재 진행 중인 탐험이 없습니다."),

	// Quest
	QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 퀘스트를 찾을 수 없습니다."),
	USER_QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 퀘스트를 찾을 수 없습니다."),
	NOT_COMPLETED(HttpStatus.BAD_REQUEST, "퀘스트가 완료되지 않았습니다."),
	REWARD_ALREADY_CLAIMED(HttpStatus.CONFLICT, "보상을 이미 수령하였습니다."),

	// MissionProof
	NOT_FOUND_MISSION_PROOF(HttpStatus.NOT_FOUND, "존재하지 않는 미션 인증글입니다."),
	FORBIDDEN_UPDATE_MISSION_PROOF(HttpStatus.FORBIDDEN, "미션 인증글 수정 권한이 없습니다."),
	FORBIDDEN_DELETE_MISSION_PROOF(HttpStatus.FORBIDDEN, "미션 인증글 삭제 권한이 없습니다."),
	INVALID_PROOF_LOCATION(HttpStatus.BAD_REQUEST, "장소 근처(200m 이내)에서만 인증할 수 있습니다."),
	LAT_LON_REQUIRED(HttpStatus.BAD_REQUEST, "PLACE 타입에서는 위도와 경도가 필수입니다."),
	PLACE_COORDINATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소의 좌표 정보를 찾을 수 없습니다."),

	// Address
	INVALID_ADDRESS_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 법정동 코드입니다."),
	ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주소 정보를 찾을 수 없습니다."),
	EMD_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 emdCd를 찾을 수 없습니다."),

	// SQL
	SQL_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SQL 파일 실행 중 오류가 발생했습니다."),


	;

	private final HttpStatus httpStatus;
	private final String message;

}
