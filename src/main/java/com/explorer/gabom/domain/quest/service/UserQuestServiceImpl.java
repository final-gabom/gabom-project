package com.explorer.gabom.domain.quest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.repository.UserQuestRepository;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQuestServiceImpl implements UserQuestService {

	private final UserQuestRepository userQuestRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void updateProgress(User user, QuestConditionType type, int step) {
		List<UserQuest> userQuests = userQuestRepository
			.findByUserAndQuest_QuestConditionTypeAndProgressStatus(user, type, ProgressStatus.IN_PROGRESS);

		for (UserQuest userQuest : userQuests) {
			userQuest.increaseProgress(step);
		}

		userQuestRepository.saveAll(userQuests);
	}

	@Override
	@Transactional
	public QuestRewardResponse claimReward(Long userId, Long userQuestId) {
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		UserQuest userQuest = userQuestRepository.findByUser_IdAndId(userId, userQuestId)
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

		return QuestRewardResponse.toDto(userQuest.getQuest());
	}
}
