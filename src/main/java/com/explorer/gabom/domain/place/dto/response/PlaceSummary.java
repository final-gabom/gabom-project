package com.explorer.gabom.domain.place.dto.response;

import com.explorer.gabom.domain.file.dto.ThumbnailDto;
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
}