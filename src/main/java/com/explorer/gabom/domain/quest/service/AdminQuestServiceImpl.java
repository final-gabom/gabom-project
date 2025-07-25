package com.explorer.gabom.domain.quest.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequest;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequest;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestDeleteResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponse;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.AdminTitleRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminQuestServiceImpl implements AdminQuestService {

	private final QuestRepository questRepository;
	private final AdminTitleRepository adminTitleRepository;

	@Override
	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_QUEST_CREATED)
	public QuestCreateResponse createQuest(QuestCreateRequest dto) {
		Title rewardTitle = adminTitleRepository.findById(dto.getRewardTitleId())
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
		return QuestCreateResponse.toDto(saved);
	}

	@Override
	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_QUEST_UPDATED)
	public QuestUpdateResponse updateQuest(Long questId, QuestUpdateRequest dto) {
		Quest quest = questRepository.findById(questId)
									 .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));

		Title rewardTitle = null;
		if (dto.getRewardTitleId() != null) {
			rewardTitle = adminTitleRepository.findById(dto.getRewardTitleId())
											  .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));
		}

		quest.update(dto, rewardTitle);
		return QuestUpdateResponse.toDto(quest);
	}

	@Override
	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_QUEST_DELETED)
	public QuestDeleteResponse deleteQuest(Long questId) {
		Quest quest = questRepository.findById(questId)
									 .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));

		questRepository.delete(quest);
		return QuestDeleteResponse.fromId(questId);
	}

}
