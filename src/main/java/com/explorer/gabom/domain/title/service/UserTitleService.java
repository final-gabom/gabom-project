package com.explorer.gabom.domain.title.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.title.repository.UserTitleRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTitleService {
	private final UserTitleRepository userTitleRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@ActivityLoggable(ActivityType.VIEW_USER_TITLES)
	public List<UserTitleResponse> getUserTitles(Long userId) {
		log.info("<칭호조회> 요청 - userId: {}", userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				log.warn("<칭호조회> 실패 - 존재하지 않는 ID: {}", userId);
				return new CustomException(ErrorCode.USER_NOT_FOUND);
			});

		List<UserTitle> userTitles = userTitleRepository.findByUser(user);
		log.info("<칭호조회> 성공 - 조회된 ID: {}", userId);
		return userTitles.stream()
			.map(UserTitleResponse::toDto)
			.collect(Collectors.toList());
	}
}
