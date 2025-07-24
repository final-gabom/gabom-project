package com.explorer.gabom.domain.quest.service;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponseDto;

public interface QuestService {
	QuestCreateResponseDto createQuest(QuestCreateRequestDto dto);
}
