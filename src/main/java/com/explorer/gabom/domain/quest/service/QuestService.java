package com.explorer.gabom.domain.quest.service;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequestDto;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponseDto;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponseDto;

public interface QuestService {
	QuestCreateResponseDto createQuest(QuestCreateRequestDto dto);

	QuestUpdateResponseDto updateQuest(Long questId, QuestUpdateRequestDto dto);
}
