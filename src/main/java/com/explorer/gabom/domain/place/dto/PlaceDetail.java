package com.explorer.gabom.domain.place.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceFile;
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
public class PlaceDetail {

	@Schema(description = "장소 ID", example = "1")
	private Long id;

	@Schema(description = "장소 제목", example = "한강공원 반포지구")
	private String title;

	@Schema(description = "주소 정보")
	private AddressDto address;

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

	public static PlaceDetail toDto(Place place, AddressDto addressDto) {
		if (place == null) {
			return null;
		}

		return PlaceDetail.builder()
						  .id(place.getId())
						  .title(place.getTitle())
						  .address(addressDto)

						  .missionProofCount(getMissionProofCount(place))
						  .avgScore(getAvgScore(place))

						  .content(place.getContent())
						  .proofMethod(place.getProofMethod())
						  .viewCount(getViewCount(place))

						  .createdAt(place.getCreatedAt())
						  .updatedAt(place.getUpdatedAt())

						  .writer(getWriter(place))
						  .files(getFiles(place))

						  .build();
	}

	public static PlaceDetail toDto(Place place) {
		if (place == null) {
			return null;
		}

		return PlaceDetail.builder()
						  .id(place.getId())
						  .title(place.getTitle())
						  .address(AddressDto.toDto(place.getAddress()))

						  .missionProofCount(getMissionProofCount(place))
						  .avgScore(getAvgScore(place))

						  .content(place.getContent())
						  .proofMethod(place.getProofMethod())
						  .viewCount(getViewCount(place))

						  .createdAt(place.getCreatedAt())
						  .updatedAt(place.getUpdatedAt())

						  .writer(getWriter(place))
						  .files(getFiles(place))

						  .build();
	}

	private static int getMissionProofCount(Place place) {
		return Optional.ofNullable(place.getMissionProofs())
					   .map(List::size)
					   .orElse(0);
	}

	private static double getAvgScore(Place place) {
		return Optional.ofNullable(place.getMissionProofs())
					   .orElse(Collections.emptyList())
					   .stream()
					   .mapToDouble(MissionProof::getStarRating)
					   .average()
					   .orElse(0.0);
	}

	private static int getViewCount(Place place) {
		return Optional.ofNullable(place.getViewCount())
					   .orElse(0);
	}

	private static UserSummaryDto getWriter(Place place) {
		return Optional.ofNullable(place.getUser())
					   .map(UserSummaryDto::toDto)
					   .orElse(null);
	}

	private static List<FileResponseDto> getFiles(Place place) {
		return Optional.ofNullable(place.getFiles())
					   .orElse(Collections.emptyList())
					   .stream()
					   .map(PlaceFile::getFile)
					   .filter(file -> file != null && !file.isDeleted())
					   .sorted(Comparator.comparingInt(AttachmentFile::getOrderIdx))
					   .map(FileResponseDto::toDto)
					   .collect(Collectors.toList());
	}
}