package com.explorer.gabom.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.dto.response.AddressCreateResponse;
import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;
import com.explorer.gabom.domain.user.entity.User;
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
public class UserController implements UserControllerDocs {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserDto>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		UserDto userDto = userService.getUser(userDetails.getUser());
		return ResponseEntity.ok(ApiResponse.success("내 프로필 조회를 성공하였습니다.", userDto));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long userId) {
		UserDto response = userService.getUser(userId);
		return ResponseEntity.ok(ApiResponse.success("회원 정보 조회를 성공하였습니다.", response));
	}

	// User 프로필 수정
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UserDto>> updateUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserUpdateRequest updateRequest) {
		User user = userDetails.getUser();
		UserDto updateResponse = userService.updateUser(user, updateRequest);
		return ResponseEntity.ok(ApiResponse.success("프로필 수정을 완료하였습니다.", updateResponse));
	}

	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userDetails.getUser();
		userService.deleteUser(user);
		return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
	}

	@PatchMapping("/me/titles")
	public ResponseEntity<ApiResponse<UpdateMainTitleResponse>> updateTitle(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UpdateMainTitleRequest request) {

		UpdateMainTitleResponse response = userService.updateMainTitle(userDetails.getUser(), request.getTitleId());
		return ResponseEntity.ok(ApiResponse.success("칭호를 변경하였습니다.", response));
	}

	@PatchMapping("/me/password")
	public ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
															@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
		log.info("비밀번호 변경 요청: userId={}", userDetails.getUser().getId());
		userService.updatePassword(userDetails.getUser(), passwordUpdateRequest);
		return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
	}

	@PutMapping("/me/address")
	public ResponseEntity<ApiResponse<AddressCreateResponse>> updateAddress(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid AddressRequest request) {
		log.info("주소 변경 요청: userId={}", userDetails.getUser().getId());
		AddressCreateResponse response = userService.updateUserAddress(userDetails.getUser(), request);
		return ResponseEntity.ok(ApiResponse.success("주소 등록을 성공했습니다.", response));
	}
}