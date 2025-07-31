package com.explorer.gabom.domain.place.dto.response;

import com.explorer.gabom.domain.file.dto.ThumbnailDto;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceSummary {
	private final Long placeId;
	private final String title;
	private final String address;
	private final Double latitude;
	private final Double longitude;
	private final Integer missionProofCount;
	private final Double avgRating;
	private final Integer viewCount;
	private final UserSummaryDto writer;
	private final ThumbnailDto thumbnail;
	private final Double distance;

	public static PlaceSummary toDto(Place place, Double distance) {
		// 첫 번째 파일이 있으면 ThumbnailDto 변환, 없으면 null
		ThumbnailDto thumb = null;
		if (place.getFirstFile() != null) {
			thumb = ThumbnailDto.toDto(place.getFirstFile());
		}

		return PlaceSummary.builder()
						   .placeId(place.getId())
						   .title(place.getTitle())
						   .address(place.getAddress())
						   .latitude(place.getLat())
						   .longitude(place.getLng())
						   .missionProofCount(place.getMissionProofs().size())
						   .avgRating(0.0)   // TODO: 엔티티에 getAvgRating() 구현 필요
						   .viewCount(place.getViewCount())
						   .writer(UserSummaryDto.toDto(place.getUser()))
						   .thumbnail(thumb)
						   .distance(distance)
						   .build();
	}
}