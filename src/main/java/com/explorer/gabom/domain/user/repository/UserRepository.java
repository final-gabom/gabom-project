package com.explorer.gabom.domain.user.repository;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.type.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByIdAndStatus(Long userId, UserStatus status);
}
