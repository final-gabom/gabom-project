package com.explorer.gabom.domain.quest.dto.request;

import com.explorer.gabom.domain.quest.type.QuestConditionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestCreateRequestDto {

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "설명은 필수입니다.")
	private String description;

	@NotNull(message = "퀘스트 타입은 필수입니다.")
	private QuestConditionType questConditionType;

	@NotNull(message = "달성 조건은 필수입니다.")
	private Integer acquireCondition;

	@NotNull(message = "포인트는 필수입니다.")
	private Integer rewardPoint;

	@NotNull(message = "경험치는 필수입니다.")
	private Integer rewardExp;

	@NotNull(message = "보상 칭호 ID는 필수입니다.")
	private Long rewardTitleId;
}
