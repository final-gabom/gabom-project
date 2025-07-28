package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMissionProofResponse {

	private Long missionProofId;
	private int rewardPoint;

	private Long writerId;
	private String fieldType;
	private String writerNickname;
	private int writerLevel;
	private String writerProfileImageUrl;

	private String title;
	private String content;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}