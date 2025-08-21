package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofResponse implements TargetIdentifiable {

	@Schema(description = "장소 ID")
	private Long id;
	@Schema(description = "필드 타입")
	private MissionProofType fieldType;

	@Schema(description = "작성자 정보")
	private UserSummaryDto writer;

	@Schema(description = "인증글 제목", example = "강남역에서 인증했어요!")
	private String title;
	@Schema(description = "인증글 내용", example = "진짜 재밌고 감동적이었음.")
	private String content;

	@Schema(description = "생성일")
	private LocalDateTime createdAt;
	@Schema(description = "수정일")
	private LocalDateTime updatedAt;

	@Schema(description = "이미지 목록")
	private List<FileResponseDto> profileImages;

	public static CreateMissionProofResponse toDto(MissionProof missionProof) {
		List<FileResponseDto> profileImages = missionProof.getImageFiles().stream()
														  .map(FileResponseDto::toDto)
														  .collect(Collectors.toList());
		return CreateMissionProofResponse.builder()
										 .id(missionProof.getId())
										 .fieldType(missionProof.getFieldType())
										 .writer(UserSummaryDto.toDto(missionProof.getUser()))
										 .title(missionProof.getTitle())
										 .content(missionProof.getContent())
										 .createdAt(missionProof.getCreatedAt())
										 .updatedAt(missionProof.getUpdatedAt())
										 .profileImages(profileImages)
										 .build();
	}

	@Override
	public Long getTargetId() {
		return this.id;
	}
}
