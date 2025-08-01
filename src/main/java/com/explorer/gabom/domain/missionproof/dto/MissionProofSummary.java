package com.explorer.gabom.domain.missionproof.dto;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionProofSummary {
	private final Long id;
	private final MissionProofType type;
	private final UserSummaryDto writer;
	private final String title;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	private final String imgUrl; // 대표 이미지 1개 (선택적으로 보여주기 위함)

	@Builder
	public MissionProofSummary(Long id, MissionProofType type, UserSummaryDto writer,
							   String title, LocalDateTime createdAt,
							   LocalDateTime updatedAt, String imgUrl) {
		this.id = id;
		this.type = type;
		this.writer = writer;
		this.title = title;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.imgUrl = imgUrl;
	}

	public static MissionProofSummary toDto(MissionProof entity) {
		return MissionProofSummary.builder()
								  .id(entity.getId())
								  .type(entity.getFieldType())
								  .writer(UserSummaryDto.toDto(entity.getUser()))
								  .title(entity.getTitle())
								  .createdAt(entity.getCreatedAt())
								  .updatedAt(entity.getUpdatedAt())
								  .imgUrl(null) // 대표 이미지 추후 확장 가능
								  .build();
	}
}