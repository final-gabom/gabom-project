package com.explorer.gabom.domain.place.dto;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.file.dto.ThumbnailDto;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "장소 요약 정보")
public class PlaceSummary {

	@Schema(description = "장소 ID", example = "1")
	private final Long placeId;

	@Schema(description = "장소 제목", example = "한강공원 반포지구")
	private final String title;
	@Schema(description = "해당 장소에서 수행된 인증 미션 수", example = "3")
	private final Integer missionProofCount;
	@Schema(description = "평균 별점", example = "4.5")
	private final Double avgRating;
	@Schema(description = "장소 조회수", example = "123")
	private final Integer viewCount;
	@Schema(description = "작성자 요약 정보")
	private final UserSummaryDto writer;
	@Schema(description = "대표 썸네일 이미지 정보")
	private final ThumbnailDto thumbnail;
	@Schema(description = "사용자로부터의 거리 (km)", example = "1.23")
	private final Double distance;
	@Schema(description = "주소 정보")
	private AddressDto address;

	public static PlaceSummary toDto(Place place, Double distance) {
		// 첫 번째 파일이 있으면 ThumbnailDto 변환, 없으면 null
		ThumbnailDto thumb = null;
		if (place.getFirstFile() != null) {
			thumb = ThumbnailDto.toDto(place.getFirstFile());
		}

		return PlaceSummary.builder()
						   .placeId(place.getId())
						   .title(place.getTitle())
						   .address(AddressDto.toDto(place.getAddress()))
						   .missionProofCount(place.getMissionProofs().size())
						   .avgRating(0.0) // TODO: 엔티티에 getAvgRating() 구현 필요
						   .viewCount(place.getViewCount())
						   .writer(UserSummaryDto.toDto(place.getUser()))
						   .thumbnail(thumb)
						   .distance(distance)
						   .build();
	}
}