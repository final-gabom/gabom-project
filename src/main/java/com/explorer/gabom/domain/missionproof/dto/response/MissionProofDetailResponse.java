package com.explorer.gabom.domain.missionproof.dto.response;


import java.time.LocalDateTime;
import java.util.List;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor

@Schema(description = "미션 인증글 상세 조회 응답")
public class MissionProofDetailResponse {
	@Schema(description = "인증글 ID")
	private Long id;

	@Schema(description = "인증 대상 타입 (예: PLACE)")
	private MissionProofType fieldType;

	@Schema(description = "작성자 정보")
	private UserSummaryDto writer;

	@Schema(description = "제목")
	private String title;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "생성일")
	private LocalDateTime createdAt;
	@Schema(description = "수정일")
	private LocalDateTime updatedAt;

	@Schema(description = "인증글 이미지 리스트")
	private List<FileResponseDto> profileImages;

	public static MissionProofDetailResponse toDto(MissionProof missionProof, List<FileResponseDto> profileImages) {
		return MissionProofDetailResponse.builder()
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
