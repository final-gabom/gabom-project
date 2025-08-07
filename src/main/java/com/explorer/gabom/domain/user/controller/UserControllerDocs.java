package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "UserAPI Document", description = "유저 정보 조회 및 수정 관련 API")
@RequestMapping("/api/users")
public interface UserControllerDocs {
	@Operation(
		summary = "내 프로필 조회",
		description = "현재 로그인된 사용자의 프로필 정보를 조회합니다."
	)
	@ApiResponse(responseCode = "200", description = "내 프로필을 성공적으로 조회했습니다.")
	@ApiResponse(responseCode = "403", description = "비밀번호가 일치하지 않습니다.")
	ResponseEntity<?> getMyProfile(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

	@Operation(
		summary = "회원 정보 조회",
		description = "다른 사용자의 프로필 정보를 조횝합니다."
	)
	@ApiResponse(responseCode = "200", description = "회원 정보를 성공적으로 조회했습니다.")
	@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
	ResponseEntity<?> getUser(@Parameter(description = "회원 ID", example = "1") @PathVariable Long userId);

	@Operation(
		summary = "프로필 수정",
		description = "현재 로그인된 사용자의 프로필 정보를 수정합니다. \n"
			+ "해당 api에서는 프로필 이미지과 닉네임만 수정이 가능합니다.\n"
			+ "칭호와 주소는 각 별도의 api를 사용하여 수정할 수 있습니다."
	)
	@ApiResponse(responseCode = "200", description = "프로필 수정을 성공했습니다.")
	@ApiResponse(responseCode = "409", description = "이미 등록된 닉네임입니다.")
	@ApiResponse(responseCode = "404", description = "파일을 찾을 수 없습니다.")
	ResponseEntity<?> updateUser(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserUpdateRequest updateRequest);

	@Operation(
		summary = "회원 탈퇴",
		description = "현재 로그인된 사용자의 정보를 삭제합니다."
	)
	@ApiResponse(responseCode = "200", description = "회원 탈퇴를 성공했습니다.")
	ResponseEntity<?> deleteUser(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

	@Operation(
		summary = "대표 칭호 변경",
		description = "현재 로그인된 사용자의 대표 칭호를 변경합니다."
	)
	@ApiResponse(responseCode = "200", description = "칭호를 성공적으로 변경했습니다.")
	@ApiResponse(responseCode = "404", description = "해당 칭호를 찾을 수 없습니다.")
	@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
	ResponseEntity<?> updateTitle(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UpdateMainTitleRequest request);

	@Operation(
		summary = "비밀번호 변경",
		description = "현재 로그인된 사용자의 비밀번호를 변경합니다."
	)
	@ApiResponse(responseCode = "200", description = "비밀번호 변경을 성공했습니다.")
	@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
	ResponseEntity<?> updatePassword(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody PasswordUpdateRequest passwordUpdateRequest);

    @Operation(
        summary = "주소 등록",
        description = "현재 로그인된 사용자의 주소를 등록합니다."
    )
    @ApiResponse(responseCode = "200", description = "주소 등록을 성공했습니다.")
    @ApiResponse(responseCode = "404", description = "해당 주소 코드를 찾을 수 없습니다.")
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    ResponseEntity<?> updateAddress(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody AddressRequest addressRequest);
}
