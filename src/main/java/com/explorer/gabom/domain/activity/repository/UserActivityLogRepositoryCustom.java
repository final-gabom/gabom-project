package com.explorer.gabom.domain.activity.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.activity.entity.UserActivityLog;

public interface UserActivityLogRepositoryCustom {
	Page<UserActivityLog> searchMyLogs(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}