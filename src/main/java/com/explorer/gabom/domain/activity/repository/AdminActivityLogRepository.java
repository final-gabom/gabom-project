package com.explorer.gabom.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.activity.entity.AdminActivityLog;

public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Long> {
}
