package com.explorer.gabom.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.activity.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
	
}
