package com.explorer.gabom.domain.place.dto.response;

import com.explorer.gabom.domain.place.entity.Place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaceDetailResponse {
	private Long placeId;
	private String title;
	private String address;
	private String distance; // 문자열로 반환 (예: "0.98km")
	private int point;
	private String proofMethod;
	private String content;
	private int viewCount;

	// 거리 포함하는 정적 팩토리 메서드
	public static PlaceDetailResponse from(Place place, Double userLat, Double userLng) {
		String distanceStr = "";
		if (userLat != null && userLng != null && place.getLat() != null && place.getLng() != null) {
			double distance = calculateDistance(userLat, userLng, place.getLat(), place.getLng());
			distanceStr = distance + "km";
		}

		return PlaceDetailResponse.builder()
								  .placeId(place.getId())
								  .title(place.getTitle())
								  .address(place.getAddress())
								  .distance(distanceStr)
								  // .point(place.getPoint()) TODO : 포인트 기능 추가 시 같이 구현
								  .proofMethod(place.getProofMethod())
								  .content(place.getContent())
								  .viewCount(place.getViewCount())
								  .build();
	}

	// Haversine 거리 계산 (소수점 3자리 반올림)
	private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // 지구 반지름 km
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return Math.round(R * c * 1000.0) / 1000.0; // km 단위, 소수점 3자리
	}

}
