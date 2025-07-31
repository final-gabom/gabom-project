package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofResponse {

	private Long id;
	private MissionProofType fieldType;

	private UserSummaryDto writer;

	private String title;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

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
}
