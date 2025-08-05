package com.explorer.gabom.domain.missionproof.dto.response;

import java.util.List;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "미션 인증글 상세 조회 응답 DTO")
@Getter
@Builder
public class SwaggerMissionProofResponse {
	@Schema(description = "인증글 제목", example = "강남역에서 인증했어요!")
	private String title;

	@Schema(description = "인증글 내용", example = "진짜 재밌고 감동적이었음.")
	private String content;

	@Schema(description = "작성자 정보")
	private UserSummaryDto user;

	@Schema(description = "이미지 목록")
	private List<FileResponseDto> profileImages;

	@Schema(description = "별점", example = "4")
	private Integer starRating;

	@Schema(description = "조회수", example = "123")
	private int viewCount;

}
