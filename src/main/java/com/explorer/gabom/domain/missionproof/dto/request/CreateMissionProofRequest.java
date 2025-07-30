package com.explorer.gabom.domain.missionproof.dto.request;

import java.util.List;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofRequest {
	@NotNull(message = "필드 타입은 필수입니다.")
	private MissionProofType fieldType; // PLACE 또는 EVENT

	@NotNull(message = "타겟 ID는 필수입니다.")
	private Long targetId;

	@NotNull(message = "데이터는 필수입니다.")
	private Data data;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Data {
		@NotNull(message = "제목은 필수입니다.")
		private String title;

		private List<String> imageId;

		@NotNull(message = "본문은 필수입니다.")
		private String content;

		@NotNull(message = "별점은 필수입니다.")
		@Min(value = 1, message = "별점은 최소 1 이상이어야 합니다.")
		@Max(value = 5, message = "별점은 최대 5 이하여야 합니다.")
		private Integer starRating;
	}

}
