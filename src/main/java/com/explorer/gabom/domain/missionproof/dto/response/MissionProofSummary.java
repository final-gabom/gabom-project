package com.explorer.gabom.domain.missionproof.dto.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MissionProofSummary {

	@Schema(description = "인증글 ID", example = "100")
	private final Long id;

	@Schema(description = "필드 타입 (PLACE / EVENT)", example = "PLACE")
	private final MissionProofType fieldType;

	@Schema(description = "작성자 정보")
	private final UserSummaryDto writer;

	@Schema(description = "인증글 제목", example = "숨겨진 명소 탐험기")
	private final String title;

	@Schema(description = "생성일", example = "2025-08-01T12:00:00")
	private final LocalDateTime createdAt;

	@Schema(description = "수정일", example = "2025-08-02T14:30:00")
	private final LocalDateTime updatedAt;

	@Schema(description = "첨부 이미지 파일 경로 리스트")
	private final List<String> imageFiles;

	public static MissionProofSummary toDto(MissionProof missionProof) {
		if (missionProof == null) return null;

		List<String> imagePaths = missionProof.getImageFiles() != null
								  ? missionProof.getImageFiles().stream()
												.map(AttachmentFile::getFilePath)
												.toList()
								  : Collections.emptyList();

		return new MissionProofSummary(
			missionProof.getId(),
			missionProof.getFieldType(),
			UserSummaryDto.toDto(missionProof.getUser()), // user → UserSummaryDto 변환
			missionProof.getTitle(),
			missionProof.getCreatedAt(),
			missionProof.getUpdatedAt(),
			imagePaths
		);
	}
}