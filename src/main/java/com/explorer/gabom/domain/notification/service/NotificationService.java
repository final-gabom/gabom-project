package com.explorer.gabom.domain.notification.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.notification.dto.NotificationResponseDto;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.repository.NotificationRepository;
import com.explorer.gabom.domain.notification.type.NotificationType;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messagingTemplate;

	@Transactional
	public Notification notify(Long userId, NotificationType type, String message, String link) {
		// 유저 조회 (알림 받을 대상)
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 알림 생성
		Notification notification = Notification.of(user, type, message, link);

		// 알림 저장 후 반환 → saved 변수에 담기
		Notification saved = notificationRepository.save(notification);

		//  WebSocket 실시간 전송
		messagingTemplate.convertAndSendToUser(
			user.getId().toString(),           // Principal.getName()과 일치해야 함
			"/queue/notifications",           // 클라이언트에서 구독할 경로
			NotificationResponseDto.from(saved)
		);

		return saved;

		// TODO : WebSocket 또는 푸시 알림 등으로 전송 처리 기능
	}

	// 알림 전체 조회
	@Transactional(readOnly = true)
	public Page<NotificationResponseDto> getNotifications(Long userId, Pageable pageable) {
		return notificationRepository.findAllByReceiverId(userId, pageable)
									 .map(NotificationResponseDto::from);
	}

	// 안 읽은 알림 수 조회
	@Transactional(readOnly = true)
	public long getUnreadCount(Long userId) {
		return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
	}

	// 알림 읽음 처리
	@Transactional
	public void markAsRead(Long notificationId, Long userId) {
		Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, userId)
														  .orElseThrow(
															  () -> new CustomException(NOTIFICATION_NOT_FOUND));
		notification.markAsRead();
	}
}
