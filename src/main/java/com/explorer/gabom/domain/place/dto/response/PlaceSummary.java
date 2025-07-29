package com.explorer.gabom.domain.place.dto.response;

import lombok.Builder;

@Builder
public record PlaceSummary(
	Long placeId,
	String title,
	String address,
	Double latitude,
	Double longitude,
	String imageUrl,
	Integer proofCount,
	Double avgRating,
	Integer viewCount,
	Long writerId,
	String nickname,
	Integer level,
	String writerTitle
) {}