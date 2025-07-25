package com.explorer.gabom.domain.quest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestPage;
import com.explorer.gabom.domain.quest.repository.QuestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepository;

	@Override
	@ActivityLoggable(ActivityType.QUEST_VIEWED)
	public QuestPage getQuestPage(Pageable pageable) {
		Page<QuestDto> questDtoPage = questRepository.findAll(pageable)
													 .map(QuestDto::toDto);
		return QuestPage.toDto(questDtoPage);
	}

}
