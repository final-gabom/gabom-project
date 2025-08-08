package com.explorer.gabom.domain.quest.service;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.global.dto.PageResponse;

public interface QuestService {
	PageResponse<QuestDto> getQuestPage(Pageable pageable);

	QuestDto getQuestById(Long questId);

}
