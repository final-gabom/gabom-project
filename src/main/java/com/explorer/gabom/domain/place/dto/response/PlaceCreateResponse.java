package com.explorer.gabom.domain.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "장소 등록 응답 정보")
public class PlaceCreateResponse {

	@Schema(description = "생성된 장소 ID", example = "1")
	private final Long id;
}