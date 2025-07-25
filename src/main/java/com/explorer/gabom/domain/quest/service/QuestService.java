package com.explorer.gabom.domain.quest.service;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.quest.dto.response.QuestPage;

public interface QuestService {
	QuestPage getQuestPageByFilter(Pageable pageable, String search);
}
