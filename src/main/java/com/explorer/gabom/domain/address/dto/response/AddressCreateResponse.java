package com.explorer.gabom.domain.address.dto.response;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.entity.Eupmyeondong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "주소 저장 응답 DTO")
public class AddressCreateResponse {

	@Schema(description = "주소 ID", example = "123")
	private Long id;

	@Schema(description = "연관된 테이블 타입", example = "USER")
	private String addressTypeCd;

	@Schema(description = "연관된 테이블 내 식별자", example = "1")
	private Long targetId;

	@Schema(description = "법정동 코드 (읍면동 코드 전체)", example = "1101050000")
	private String emdCd;

	@Schema(description = "읍면동 이름", example = "청운효자동")
	private String emdNm;

	@Schema(description = "상세 주소", example = "와르르멘션 204호")
	private String detail;

	@Schema(description = "위도", example = "37.498095")
	private Double lat;

	@Schema(description = "경도", example = "127.027610")
	private Double lng;

	public static AddressCreateResponse toDto(Address address, Eupmyeondong eupmyeondong) {
		return AddressCreateResponse.builder()
									.id(address.getId())
									.addressTypeCd(address.getAddressTypeCd())
									.targetId(address.getTargetId())
									.emdCd(address.getEmdCd())
									.emdNm(eupmyeondong.getEmdNm())
									.detail(address.getDetail())
									.lat(address.getLat())
									.lng(address.getLng())
									.build();
	}
}