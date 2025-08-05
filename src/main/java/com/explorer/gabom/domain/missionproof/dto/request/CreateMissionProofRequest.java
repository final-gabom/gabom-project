package com.explorer.gabom.domain.missionproof.dto.request;

import java.util.List;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "미션 인증글 생성 요청 DTO")
public class CreateMissionProofRequest {
	@NotNull(message = "필드 타입은 필수입니다.")
	@Schema(description = "인증 대상 타입 (예: 장소)", example = "PLACE")
	private MissionProofType fieldType; // PLACE 또는 EVENT

	@NotNull(message = "타겟 ID는 필수입니다.")
	@Schema(description = "인증 대상 ID", example = "123")
	private Long targetId;

	@NotNull(message = "데이터는 필수입니다.")
	@Schema(description = "인증글 데이터")
	private Data data;

	@Getter
	@Builder
	@AllArgsConstructor
	@Schema(description = "인증글 데이터 객체")
	public static class Data {
		@NotNull(message = "제목은 필수입니다.")
		@Schema(description = "인증글 제목", example = "강남역에서 인증했어요!")
		private String title;

		@Schema(description = "이미지 ID 리스트", example = "[\"uuid1\", \"uuid2\"]")
		private List<String> imageId;

		@NotNull(message = "본문은 필수입니다.")
		@Schema(description = "인증글 내용", example = "정말 재밌는 탐험이었어요.")
		private String content;

		@NotNull(message = "별점은 필수입니다.")
		@Min(value = 1, message = "별점은 최소 1 이상이어야 합니다.")
		@Max(value = 5, message = "별점은 최대 5 이하여야 합니다.")
		@Schema(description = "별점 (1~5)", example = "5")
		private Integer starRating;
	}

}
