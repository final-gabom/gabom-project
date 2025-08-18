package com.explorer.gabom.domain.place.dto.request;

import com.explorer.gabom.domain.address.dto.request.AddressRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 수정 요청 정보")
public class PlaceUpdateRequest {

	@Schema(description = "장소의 제목", example = "진짜 맛있는 국밥집")
	private String title;

	@Schema(description = "장소의 주소 정보")
	private AddressRequest address;

	@Schema(description = "장소 설명", example = "쫄면이 진짜 맛있는 집이에요.")
	private String content;

	@Schema(description = "인증 방법", example = "사진 인증")
	private String proofMethod;
}