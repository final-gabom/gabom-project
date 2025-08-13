package com.explorer.gabom.domain.place.dto.request;

import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

	public boolean hasLocation() {
		return lat != null && lng != null;
	}

	public boolean hasAnyAddress() {
		return emdCd != null || sggCd != null || sdCd != null;
	}
}
