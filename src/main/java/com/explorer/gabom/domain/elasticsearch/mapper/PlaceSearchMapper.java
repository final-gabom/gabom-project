package com.explorer.gabom.domain.elasticsearch.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.elasticsearch.entity.PlaceSearchDoc;
import com.explorer.gabom.domain.place.entity.Place;

import io.micrometer.common.lang.Nullable;

@Component
public class PlaceSearchMapper {

	public PlaceSearchDoc toDoc(Place p, @Nullable Address a) {
		String full = null;
		Double lat = null, lon = null;
		String emd = null;

		if (a != null) {
			emd = a.getEmdCd();
			lat = a.getLat();
			lon = a.getLng();
			full = Stream.of(a.getSdCd(), a.getSggCd(), a.getEmdCd(), a.getDetail())
						 .filter(Objects::nonNull)
						 .collect(Collectors.joining(" "));
		}

		Instant created = null;
		if (p.getCreatedAt() != null) {
			created = p.getCreatedAt().atZone(ZoneId.of("UTC")).toInstant();
		}

		return PlaceSearchDoc.builder()
							 .id(String.valueOf(p.getId()))
							 .title(p.getTitle())
							 .addressFull(full)
							 .emdCd(emd)
							 .popularity(p.getViewCount() != null ? p.getViewCount().longValue() : 0L)
							 .status(p.getStatus() != null ? p.getStatus().name() : null)
							 .createdAt(created)
							 .lat(lat)
							 .lon(lon)
							 .build();
	}
}
