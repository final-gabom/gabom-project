package com.explorer.gabom.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.type.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndStatus(Long userId, UserStatus status);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

}
