package com.explorer.gabom.domain.missionproof.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionProofSummaryResponse {

	private Long id;
	private String title;
	private String content;
	private Integer starRating;
	private String nickname;
	private List<String> imageFilePaths;

	public static MissionProofSummaryResponse toDto(MissionProof missionProof) {
		List<String> filePaths = missionProof.getImageFiles().stream()
											 .map(AttachmentFile::getFilePath)
											 .collect(Collectors.toList());

		return new MissionProofSummaryResponse(
			missionProof.getId(),
			missionProof.getTitle(),
			missionProof.getContent(),
			missionProof.getStarRating(),
			missionProof.getUser().getNickname(),
			filePaths
		);
	}
}