package com.explorer.gabom.domain.user.controller;


import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "UserAPI Document", description = "유저 정보 조회 및 수정 관련 API")
@RequestMapping("/api/users")
public interface UserControllerDocs {
    @Operation(summary = "내 프로필 조회")
    @ApiResponse(responseCode = "200", description = "내 프로필을 성공적으로 조회했습니다.")
    ResponseEntity<?> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "회원 정보 조회")
    @ApiResponse(responseCode = "200", description = "회원 정보를 성공적으로 조회했습니다.")
    ResponseEntity<?> getUser(@Parameter(description = "회원 ID", example = "1") @PathVariable Long userId);

    @Operation(summary = "프로필 수정")
    @ApiResponse(responseCode = "200", description = "프로필 수정을 성공했습니다.")
    ResponseEntity<?> updateUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequest updateRequest);

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴를 성공했습니다.")
    ResponseEntity<?> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "메인 칭호 변경")
    @ApiResponse(responseCode = "200", description = "칭호를 성공적으로 변경했습니다.")
    ResponseEntity<?> updateTitle(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateMainTitleRequest request);

    @Operation(summary = "비밀번호 변경")
    @ApiResponse(responseCode = "200", description = "비밀번호 변경을 성공했습니다.")
    ResponseEntity<?> updatePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest);
}
