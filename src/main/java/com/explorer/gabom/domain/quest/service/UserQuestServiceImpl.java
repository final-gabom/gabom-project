package com.explorer.gabom.domain.quest.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.level.service.LevelService;
import com.explorer.gabom.domain.quest.dto.UserQuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.quest.repository.UserQuestRepository;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.ranking.message.ExpEventMessage;
import com.explorer.gabom.domain.ranking.message.ExpEventProducer;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQuestServiceImpl implements UserQuestService {

	private final UserQuestRepository userQuestRepository;
	private final UserRepository userRepository;
	private final QuestRepository questRepository;
	private final LevelService levelService;
	private final ExpEventProducer expEventProducer;

	@Override
	@Transactional
	public void updateProgress(User user, QuestConditionType type, int step) {
		List<Quest> quests = questRepository.findByQuestConditionTypeAndDeletedFalse(type);

		for (Quest quest : quests) {
			UserQuest userQuest = userQuestRepository.findByUserAndQuest(user, quest)
													 .orElseGet(() -> {
														 return createUserQuest(user, type, quest);
													 });

			if (userQuest.isCompleted())
				continue;

			if (userQuest.getProgressStatus() == ProgressStatus.NOT_STARTED) {
				userQuest.markInProgress();
			}

			userQuest.increaseProgress(step);
		}
	}

	private UserQuest createUserQuest(User user, QuestConditionType type, Quest quest) {
		int baseProgress = userQuestRepository.findMaxProgressByUserAndQuestType(
												  user.getId(), type)
											  .orElse(0);
		UserQuest newUserQuest = new UserQuest(user, quest);
		newUserQuest.increaseProgress(baseProgress);
		return userQuestRepository.save(newUserQuest);
	}

	@Override
	@Transactional
	public QuestRewardResponse claimReward(Long userId, Long userQuestId) {
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		UserQuest userQuest = userQuestRepository.findByUser_IdAndIdAndQuest_DeletedFalse(userId, userQuestId)
												 .orElseThrow(
													 () -> new CustomException(ErrorCode.USER_QUEST_NOT_FOUND));

		if (!userQuest.isCompleted()) {
			throw new CustomException(ErrorCode.NOT_COMPLETED);
		}
		if (userQuest.isRewardClaimed()) {
			throw new CustomException(ErrorCode.REWARD_ALREADY_CLAIMED);
		}

		userQuest.markRewardClaimed();
		user.addPoint(userQuest.getQuest().getRewardPoint());
		user.addExp(userQuest.getQuest().getRewardExp());
		user.addTitle(userQuest.getQuest().getRewardTitle());

		int exp = user.getExp();
		int level = levelService.calculateLevel(exp);
		if (level > user.getLevel()) {
			user.updateLevel(level);
		}
		expEventProducer.sendExpEvent(new ExpEventMessage(
			userId,
			exp,
			level,
			user.getNickname(),
			user.getTitle().getName(),
			user.getProfileImageId()
		));

		return QuestRewardResponse.toDto(userQuest.getQuest());
	}

	@Override
	public PageResponse<UserQuestDto> getProgress(Long userId, ProgressStatus progressStatus, Pageable pageable) {
		Page<UserQuestDto> userQuestDtoPage;

		if (progressStatus == ProgressStatus.NOT_STARTED) {
			Page<Quest> notStartedQuests = questRepository.findQuestsNotJoinedByUser(userId, pageable);
			userQuestDtoPage = notStartedQuests.map(UserQuestDto::toDto);
		} else {
			Page<UserQuest> userQuests = userQuestRepository.findUserQuests(userId, progressStatus, pageable);
			userQuestDtoPage = userQuests.map(UserQuestDto::toDto);
		}

		return PageResponse.toDto(userQuestDtoPage);
	}
}
