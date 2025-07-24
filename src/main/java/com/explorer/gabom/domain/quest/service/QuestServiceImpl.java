package com.explorer.gabom.domain.quest.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequestDto;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponseDto;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponseDto;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepository;
	private final TitleRepository titleRepository;

	@Override
	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_QUEST_CREATED)
	public QuestCreateResponseDto createQuest(QuestCreateRequestDto dto) {
		Title rewardTitle = titleRepository.findById(dto.getRewardTitleId())
										   .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));

		Quest quest = new Quest(
			dto.getTitle(),
			dto.getDescription(),
			dto.getQuestConditionType(),
			dto.getAcquireCondition(),
			dto.getRewardPoint(),
			dto.getRewardExp(),
			rewardTitle
		);

		Quest saved = questRepository.save(quest);
		return QuestCreateResponseDto.toDto(saved);
	}

	@Override
	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_QUEST_UPDATED)
	public QuestUpdateResponseDto updateQuest(Long questId, QuestUpdateRequestDto dto) {
		Quest quest = questRepository.findById(questId)
									 .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));

		Title rewardTitle = null;
		if (dto.getRewardTitleId() != null) {
			rewardTitle = titleRepository.findById(dto.getRewardTitleId())
										 .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));
		}

		quest.update(dto, rewardTitle);
		return QuestUpdateResponseDto.toDto(quest);
	}

}
