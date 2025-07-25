package com.explorer.gabom.domain.quest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestPage;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepository;

	@Override
	@ActivityLoggable(ActivityType.QUEST_VIEWED)
	public QuestPage getQuestPageByFilter(Pageable pageable, String search) {

		Page<Quest> quests = questRepository.findAllByFilters(pageable, search);

		List<QuestDto> questDtos = quests.stream()
										 .map(QuestDto::toDto)
										 .collect(Collectors.toList());

		return QuestPage.toDto(
			new PageImpl<>(questDtos, pageable, quests.getTotalElements()));
	}

}
