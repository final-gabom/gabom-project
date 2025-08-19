package com.explorer.gabom.domain.notification.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.notification.dto.response.NotificationResponseDto;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.repository.NotificationRepository;
import com.explorer.gabom.domain.notification.type.NotificationRefType;
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
		return notify(userId, type, message, link, null, null);
	}

	@Transactional
	public Notification notify(Long userId,
							   NotificationType type,
							   String message,
							   String link,
							   NotificationRefType refType,
							   Long refId) {

		// 알림 받을 유저
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		// 엔티티 생성 (refType/refId 있으면 포함)
		final Notification notification = (refType == null)
										  ? Notification.of(user, type, message, link)
										  : Notification.of(user, type, message, link, refType, refId);

		Notification saved = notificationRepository.save(notification);


		// 유저 개별 큐로 실시간 전송
		sendWebsocket(user, saved);

		return saved;
	}

	/**
	 * 필요하면 User 객체로 바로 보내는 오버로드도 제공 (쿼리 한번 줄이기용)
	 */
	@Transactional
	public Notification notify(User user,
							   NotificationType type,
							   String message,
							   String link,
							   NotificationRefType refType,
							   Long refId) {

		final Notification notification = (refType == null)
										  ? Notification.of(user, type, message, link)
										  : Notification.of(user, type, message, link, refType, refId);

		Notification saved = notificationRepository.save(notification);
		sendWebsocket(user, saved);
		return saved;
	}

	private void sendWebsocket(User user, Notification saved) {
		// Principal.getName() = userId 문자열 이어야 클라이언트 /user/queue/notifications 수신
		messagingTemplate.convertAndSendToUser(
			user.getId().toString(),
			"/queue/notifications",
			NotificationResponseDto.toDto(saved)
		);
	}

	// 알림 전체 조회
	@Transactional(readOnly = true)
	public Page<NotificationResponseDto> getNotifications(Long userId, Pageable pageable) {
		return notificationRepository.findAllByReceiverId(userId, pageable)
									 .map(NotificationResponseDto::toDto);
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
