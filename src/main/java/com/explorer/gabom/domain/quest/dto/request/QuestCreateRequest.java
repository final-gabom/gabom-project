package com.explorer.gabom.domain.quest.dto.request;

import com.explorer.gabom.domain.quest.type.QuestConditionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestCreateRequest {

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 20, message = "제목은 최대 20자까지 입력 가능합니다.")
	@Schema(description = "퀘스트 제목", example = "맛집 탐방", maxLength = 20)
	private String title;

	@NotBlank(message = "설명은 필수입니다.")
	@Size(max = 255, message = "설명은 최대 255자까지 입력 가능합니다.")
	@Schema(description = "퀘스트 설명", example = "서울의 핫플레이스 음식점을 방문하세요.", maxLength = 255)
	private String description;

	@NotNull(message = "퀘스트 타입은 필수입니다.")
	@Schema(description = "퀘스트 조건 타입", example = "PLACE")
	private QuestConditionType questConditionType;

	@NotNull(message = "달성 조건은 필수입니다.")
	@Min(value = 1, message = "달성 조건은 1 이상이어야 합니다.")
	@Schema(description = "퀘스트 달성 조건 수치", example = "3", minimum = "1")
	private Integer acquireCondition;

	@Schema(description = "퀘스트 완료 시 지급할 포인트", example = "500", minimum = "1")
	private @NotNull(message = "포인트는 필수입니다.") @Min(value = 1, message = "포인트는 1 이상이어야 합니다.") Long rewardPoint;

	@Schema(description = "퀘스트 완료 시 지급할 경험치", example = "100", minimum = "1")
	private @NotNull(message = "경험치는 필수입니다.") @Min(value = 1, message = "경험치는 1 이상이어야 합니다.") Long rewardExp;

	@NotNull(message = "보상 칭호 ID는 필수입니다.")
	@Schema(description = "퀘스트 완료 시 지급할 칭호 ID", example = "1")
	private Long rewardTitleId;
}
