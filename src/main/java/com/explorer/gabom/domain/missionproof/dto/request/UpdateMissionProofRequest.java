package com.explorer.gabom.domain.missionproof.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMissionProofRequest {
	private String title;
	private String content;
	private List<String> imgFiles;
}
