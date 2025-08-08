package com.explorer.gabom.domain.address.dto.request;

import com.explorer.gabom.domain.address.type.AddressType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Builder
@Schema(description = "주소 등록/수정 요청 DTO")
public class AddressRequest {
	@Schema(hidden = true)
	private AddressType addressTypeCd;

	@Schema(hidden = true)
	private Long targetId;

	@Schema(description = "법정동 코드 (읍면동 코드 전체)", example = "1101050000")
	@NotBlank(message = "addressCd는 필수입니다.")
	private String emdCd;

	@Schema(description = "상세 주소", example = "역삼동 123-45")
	@NotBlank(message = "상세 주소는 필수입니다.")
	private String addressDetail;

	@Schema(description = "위도", example = "37.498095")
	@NotNull(message = "lat는 필수입니다.")
	private Double lat;

	@Schema(description = "경도", example = "127.027610")
	@NotNull(message = "lng는 필수입니다.")
	private Double lng;
}
