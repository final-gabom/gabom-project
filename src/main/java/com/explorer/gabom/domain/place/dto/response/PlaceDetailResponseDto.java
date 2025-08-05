package com.explorer.gabom.domain.place.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 상세 조회 응답 정보")
public class PlaceDetailResponseDto {

	@Schema(description = "장소 ID", example = "1")
	private Long id;

	@Schema(description = "장소 제목", example = "한강공원 반포지구")
	private String title;

	@Schema(description = "장소 주소", example = "서울특별시 서초구 반포동 115-5")
	private String address;

	@Schema(description = "위도", example = "37.508987")
	private Double lat;

	@Schema(description = "경도", example = "126.995751")
	private Double lng;

	@Schema(description = "인증 미션 수행 횟수", example = "12")
	private Integer missionProofCount;

	@Schema(description = "평균 별점", example = "4.5")
	private Double avgScore;

	@Schema(description = "장소 설명", example = "야경이 멋진 곳이에요. 산책이나 자전거 타기 좋습니다.")
	private String content;

	@Schema(description = "인증 방법", example = "사진 인증")
	private String proofMethod;

	@Schema(description = "장소 조회수", example = "123")
	private Integer viewCount;

	@Schema(description = "장소 등록일", example = "2025-08-05T15:00:00")
	private LocalDateTime createdAt;

	@Schema(description = "장소 수정일", example = "2025-08-06T10:30:00")
	private LocalDateTime updatedAt;

	@Schema(description = "작성자 정보")
	private UserSummaryDto writer;

	@Schema(description = "장소에 등록된 파일 목록")
	private List<FileResponseDto> files;
}