package com.explorer.gabom.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long userId) {
		UserDto response = userService.getUser(userId);
		return ResponseEntity.ok(ApiResponse.success("회원 정보 조회를 성공하였습니다.", response));
	}

	//todo : AuthenticationPrinciple 에서 유저 아이디 가져오도록 수정
	// User 프로필 수정
	@PatchMapping("/me/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> updateUser(
		@PathVariable Long userId,
		@RequestBody UserUpdateRequest updateRequest) {
		UserDto updateResponse = userService.updateUser(userId, updateRequest);
		return ResponseEntity.ok(ApiResponse.success("프로필 수정을 완료하였습니다.", updateResponse));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
	}
}
