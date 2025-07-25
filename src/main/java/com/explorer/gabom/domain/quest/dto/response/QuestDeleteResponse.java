package com.explorer.gabom.domain.quest.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestDeleteResponse implements TargetIdentifiable {

	private Long questId;
	private LocalDateTime deletedAt;

	public static QuestDeleteResponse fromId(Long questId) {
		return QuestDeleteResponse.builder()
								  .questId(questId)
								  .deletedAt(LocalDateTime.now())
								  .build();
	}

	@Override
	public Long getTargetId() {
		return this.questId;
	}
}
