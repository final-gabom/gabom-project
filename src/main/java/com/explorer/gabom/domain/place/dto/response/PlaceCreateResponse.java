package com.explorer.gabom.domain.place.dto.response;

import com.explorer.gabom.domain.place.entity.Place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "장소 등록 응답 정보")
public class PlaceCreateResponse {

	@Schema(description = "생성된 장소 ID", example = "1")
	private final Long id;

	@Schema(description = "장소 제목", example = "뚝섬 한강공원")
	private final String title;

	@Schema(description = "장소 본문/설명", example = "한강 뷰와 카페가 인접해 있어 데이트 코스로 추천합니다.")
	private final String content;

	@Schema(description = "인증 방법", example = "사진 인증")
	private final String proofMethod;

	@Schema(description = "장소 주소", example = "서울특별시 성동구 뚝섬로 273")
	private final String address;

	@Schema(description = "위도", example = "37.547121")
	private final Double lat;

	@Schema(description = "경도", example = "127.074910")
	private final Double lng;

	@Schema(description = "장소 상태", example = "APPROVED")
	private final String status;

	public static PlaceCreateResponse toDto(Place place) {
		return PlaceCreateResponse.builder()
								  .id(place.getId())
								  .title(place.getTitle())
								  .content(place.getContent())
								  .proofMethod(place.getProofMethod())
								  .address(place.getAddress())
								  .lat(place.getLat())
								  .lng(place.getLng())
								  .status(place.getStatus().name())
								  .build();
	}
}