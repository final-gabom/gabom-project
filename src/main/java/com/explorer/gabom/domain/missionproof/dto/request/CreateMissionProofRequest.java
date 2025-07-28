package com.explorer.gabom.domain.missionproof.dto.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofRequest {

	@NotBlank(message = "필드 타입은 필수입니다.")
	private String fieldType;  // PLACE 또는 EVENT

	@NotNull(message = "대상 ID는 필수입니다.")
	private Long targetId;

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	private List<@NotBlank(message = "이미지 URL은 비어 있을 수 없습니다.") String> imageId;

	@NotBlank(message = "본문 내용은 필수입니다.")
	private String content;

	@NotNull(message = "별점은 필수입니다.")
	@Min(value = 1, message = "별점은 최소 1점이어야 합니다.")
	@Max(value = 5, message = "별점은 최대 5점이어야 합니다.")
	private Integer starRating;
}
