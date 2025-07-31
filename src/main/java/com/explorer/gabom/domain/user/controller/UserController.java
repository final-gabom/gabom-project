package com.explorer.gabom.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserDto>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		UserDto userDto = userService.getUser(userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("내 프로필 조회를 성공하였습니다.", userDto));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long userId) {
		UserDto response = userService.getUser(userId);
		return ResponseEntity.ok(ApiResponse.success("회원 정보 조회를 성공하였습니다.", response));
	}

	// todo : AuthenticationPrinciple 에서 유저 아이디 가져오도록 수정
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

	@PatchMapping("/me/titles")
	public ResponseEntity<ApiResponse<UpdateMainTitleResponse>> updateTitle(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UpdateMainTitleRequest request) {

		UpdateMainTitleResponse response = userService.updateMainTitle(userDetails.getUserId(), request.getTitleId());
		return ResponseEntity.ok(ApiResponse.success("칭호를 변경하였습니다.", response));
	}

	@PatchMapping("/me/password")
	public ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
															@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
		log.info("비밀번호 변경 요청: userId={}", userDetails.getUserId());
		userService.updatePassword(userDetails.getUserId(), passwordUpdateRequest);
		return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));

	}
}
