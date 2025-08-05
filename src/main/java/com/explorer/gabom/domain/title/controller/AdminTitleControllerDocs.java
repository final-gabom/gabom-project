package com.explorer.gabom.domain.title.controller;

import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminTitleAPI Document", description = "칭호 관련 관리자 기능 API 문서화")
public interface AdminTitleControllerDocs {

	@Operation(summary = "칭호 등록")
	@ApiResponse(responseCode = "201", description = "칭호가 성공적으로 등록되었습니다.")
	ResponseEntity<?> createTitle(TitleCreateRequest request);

	@Operation(summary = "칭호 수정")
	@ApiResponse(responseCode = "200", description = "칭호가 성공적으로 수정되었습니다.")
	@Parameter(name = "titleId", description = "수정할 칭호 ID")
	ResponseEntity<?> updateTitle(Long titleId, TitleUpdateRequest request);

	@Operation(summary = "칭호 삭제")
	@ApiResponse(responseCode = "200", description = "칭호가 성공적으로 삭제되었습니다.")
	@Parameter(name = "titleId", description = "삭제할 칭호 ID")
	ResponseEntity<?> deleteTitle(Long titleId);
}
