package com.explorer.gabom.domain.place.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 수정 요청 정보")
public class PlaceUpdateRequest {

	@Schema(description = "장소 제목", example = "진짜 맛있는 국밥집")
	private String title;

	@Schema(description = "장소 주소", example = "부산 광안리")
	private String address;

	@Schema(description = "위도", example = "35.1530")
	private Double lat;

	@Schema(description = "경도", example = "129.1180")
	private Double lng;

	@Schema(description = "장소 설명", example = "쫄면이 진짜 맛있는 집이에요.")
	private String content;

	@Schema(description = "인증 방법", example = "사진 인증")
	private String proofMethod;
}