package com.explorer.gabom.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.activity.entity.UserActivityLog;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}
