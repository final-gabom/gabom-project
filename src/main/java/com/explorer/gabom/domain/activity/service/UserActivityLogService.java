package com.explorer.gabom.domain.activity.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogListResponse;
import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.domain.activity.entity.UserActivityLog;
import com.explorer.gabom.domain.activity.repository.UserActivityLogRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityLogService {
	private final UserActivityLogRepository userActivityLogRepository;

	@Transactional(readOnly = true)
	public UserActivityLogListResponse getMyLogs(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
		log.info("<활동로그조회> 요청 - userId: {}, from: {}, to: {}, page: {}, size: {}", userId, from, to, pageable.getPageNumber(), pageable.getPageSize());

		if (userId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		Page<UserActivityLog> logs = userActivityLogRepository.searchMyLogs(userId, from, to, pageable);
		log.info("<활동로그조회> 성공 - 총 {}건", logs.getTotalElements());

		Page<UserActivityLogResponse> responsePage = logs.map(UserActivityLogResponse::toDto);
		return UserActivityLogListResponse.toDto(responsePage);
	}
}