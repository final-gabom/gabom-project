package com.explorer.gabom.domain.quest.service;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequest;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequest;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestDeleteResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponse;

public interface AdminQuestService {
	QuestCreateResponse createQuest(QuestCreateRequest dto);

	QuestUpdateResponse updateQuest(Long questId, QuestUpdateRequest dto);

	QuestDeleteResponse deleteQuest(Long questId);
}
