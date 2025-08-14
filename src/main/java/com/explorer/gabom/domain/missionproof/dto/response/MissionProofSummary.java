package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissionProofSummary {

	private Long id;
	private MissionProofType fieldType;
	private UserSummaryDto writer;
	private String title;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<String> profileImages;
}