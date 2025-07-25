package com.explorer.gabom.domain.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceCreateRequest {

	@NotBlank(message = "제목은 필수입니다.")
	private final String title;

	@NotBlank(message = "주소는 필수입니다.")
	private final String address;

	@NotNull(message = "위도 값은 필수입니다.")
	private final Double lat;

	@NotNull(message = "경도 값은 필수입니다.")
	private final Double lng;

	@NotBlank(message = "인증 방법은 필수입니다.")
	private final String proofMethod;

	@NotBlank(message = "본문은 필수입니다.")
	private final String content;
}