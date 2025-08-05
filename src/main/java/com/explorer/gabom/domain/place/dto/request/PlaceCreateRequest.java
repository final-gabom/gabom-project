package com.explorer.gabom.domain.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "장소 등록 요청 정보")
public class PlaceCreateRequest {

	@Schema(description = "장소 제목", example = "한강공원 반포지구")
	@NotBlank(message = "제목은 필수입니다.")
	private final String title;

	@Schema(description = "장소 주소", example = "서울특별시 서초구 반포동 115-5")
	@NotBlank(message = "주소는 필수입니다.")
	private final String address;

	@Schema(description = "위도", example = "37.508987")
	@NotNull(message = "위도 값은 필수입니다.")
	private final Double lat;

	@Schema(description = "경도", example = "126.995751")
	@NotNull(message = "경도 값은 필수입니다.")
	private final Double lng;

	@Schema(description = "인증 방법", example = "사진 인증")
	@NotBlank(message = "인증 방법은 필수입니다.")
	private final String proofMethod;

	@Schema(description = "장소 설명", example = "야경이 멋진 곳이에요. 산책이나 자전거 타기 좋습니다.")
	@NotBlank(message = "본문은 필수입니다.")
	private final String content;
}