package com.explorer.gabom.domain.address.dto;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.entity.Eupmyeondong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "주소 정보 DTO")
public class AddressDto {

	@Schema(description = "주소 고유 ID", example = "1")
	private final Long id;

	@Schema(description = "연관된 테이블의 타입 (ex. PLACE, USER)", example = "USER")
	private final String addressTypeCd;

	@Schema(description = "연관된 테이블 내 식별 ID", example = "23")
	private final Long targetId;

	@Schema(description = "법정동 코드 (읍면동 코드 전체)", example = "1101050000")
	private final String emdCd;

	@Schema(description = "읍면동명", example = "사직동")
	private final String emdNm;

	@Schema(description = "상세주소", example = "123-45")
	private final String detail;

	@Schema(description = "위도", example = "37.498095")
	private final Double lat;

	@Schema(description = "경도", example = "127.027610")
	private final Double lng;

	public static AddressDto toDto(Address addr) {
		return AddressDto.builder()
						 .id(addr.getId())
						 .addressTypeCd(addr.getAddressTypeCd())
						 .targetId(addr.getTargetId())
						 .emdCd(addr.getEmdCd())
						 .emdNm(addr.getEupmyeondong().getEmdNm())
						 .detail(addr.getDetail())
						 .lat(addr.getLat())
						 .lng(addr.getLng())
						 .build();
	}

	public static AddressDto toDto(Address addr, Eupmyeondong emd) {
		return AddressDto.builder()
						 .id(addr.getId())
						 .addressTypeCd(addr.getAddressTypeCd())
						 .targetId(addr.getTargetId())
						 .emdCd(emd.getEmdCd())
						 .emdNm(emd.getEmdNm())
						 .detail(addr.getDetail())
						 .lat(addr.getLat())
						 .lng(addr.getLng())
						 .build();
	}
}
