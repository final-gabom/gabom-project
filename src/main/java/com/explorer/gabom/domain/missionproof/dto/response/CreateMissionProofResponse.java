package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofResponse {


	private Long missionProofId;
	private MissionProofType fieldType;

	private UserSummaryDto writer;

	private String title;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
