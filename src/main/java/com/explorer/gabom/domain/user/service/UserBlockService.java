package com.explorer.gabom.domain.user.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.user.dto.response.UserBlockResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.entity.UserBlock;
import com.explorer.gabom.domain.user.repository.UserBlockRepository;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBlockService {
	private final UserRepository userRepository;
	private final UserBlockRepository userBlockRepository;

	// 차단 기능 (blocker 차단하는 사람, blocked 차단 당하는 사람)
	@Transactional
	public UserBlockResponse blockUser(Long userId, Long blockedId){
		if (userId.equals(blockedId)) {
			throw new CustomException(CANNOT_BLOCK_SELF);
		}
		User blocker = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
									 .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		User blocked = userRepository.findByIdAndStatus(blockedId, UserStatus.ACTIVE)
									 .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		boolean alreadyBlocked = userBlockRepository.existsByBlockerAndBlocked(blocker, blocked);
		if (alreadyBlocked) {
			throw new CustomException(ALREADY_BLOCKED_USER);
		}
			UserBlock userBlock = new UserBlock(blocker, blocked);
			userBlockRepository.save(userBlock);
			return new UserBlockResponse(blocker.getId(), blocked.getId());
	}
}
