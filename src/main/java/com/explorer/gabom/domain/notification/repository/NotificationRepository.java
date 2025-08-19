package com.explorer.gabom.domain.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	// 특정 유저가 받은 알림 목록 조회
	List<Notification> findByReceiverId(Long receiverId);

	// 특정 유저의 알림 전체 조회(최신순, 페이징)
	Page<Notification> findAllByReceiverId(Long receiverId, Pageable pageable);

	// 안 읽은 알림 수 조회
	long countByReceiverIdAndIsReadFalse(Long receiverId);

	// 특정 알림 ID + 사용자 ID로 조회 (보안용)
	Optional<Notification> findByIdAndReceiverId(Long receiverId, Long userId);
}
