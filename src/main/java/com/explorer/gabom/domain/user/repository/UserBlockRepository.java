package com.explorer.gabom.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.entity.UserBlock;

public interface UserBlockRepository extends JpaRepository <UserBlock, Long> {
	boolean existsByBlockerAndBlocked(User blocker, User blocked);
}
