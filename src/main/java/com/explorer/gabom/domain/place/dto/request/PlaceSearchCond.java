package com.explorer.gabom.domain.place.dto.request;

import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PlaceSearchCond {

	private Double lat;
	private Double lng;
	private String sdCd;
	private String sggCd;
	private String emdCd;
	private Pageable pageable;
	private String keyword;

	@Builder.Default
	private Double radiusKm = 40.0;  // null이면 기본값 사용

	public PlaceSearchCond(Double lat, Double lng, String sdCd, String sggCd, String emdCd, Pageable pageable,
						   String keyword) {
		this.lat = lat;
		this.lng = lng;
		this.sdCd = sdCd;
		this.sggCd = sggCd;
		this.emdCd = emdCd;
		this.pageable = pageable;
		this.keyword = keyword;
	}

	/** null/이상치면 기본값으로 정규화하여 반환 */
	public double getRadiusKm(double defaultKm) {
		if (radiusKm == null) return defaultKm;
		double r = Math.max(0.1, Math.min(radiusKm, 100.0)); // 0.1~100km 제한 예시
		return r;
	}

	public boolean hasLatLng() {
		return lat != null && lng != null;
	}

	public boolean hasLocation() {
		return lat != null && lng != null;
	}

	public boolean hasAnyAddress() {
		return emdCd != null || sggCd != null || sdCd != null;
	}
}
