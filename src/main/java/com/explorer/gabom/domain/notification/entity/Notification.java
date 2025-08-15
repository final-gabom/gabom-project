package com.explorer.gabom.domain.notification.entity;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;
import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Notification {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User receiver; // 알림을 받는 유저

	@Enumerated(EnumType.STRING)
	private NotificationType type; // 알림의 종류 ( 예: 인증글 작성, 퀘스트 완료 등)

	private String message; // 사용자에게 보여줄 메시지
	private String link;    // 알림 클릭 시 이동할 URL or 경로

	// 어떤 리소스에 대한 알림인지(폴리모픽 레퍼런스)
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private NotificationRefType refType = NotificationRefType.SYSTEM; // 예: EXPLORATION / AUTH_POST ...

	@Column(nullable = true)
	private Long refId; // 해당 리소스의 PK

	@Builder.Default
	@Column(nullable = false)
	private boolean isRead = false; // 읽음 여부 (true면 읽음, false면 안 읽음)

	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now(); // 알림 생성 시각

	@PrePersist
	void prePersist() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}

	public static Notification of(User receiver, NotificationType type, String message, String link) {
		return Notification.builder()
						   .receiver(receiver)
						   .type(type)
						   .message(message)
						   .link(link)
						   .refType(NotificationRefType.SYSTEM)
						   .refId(null)
						   .build();
	}

	public static Notification of(User receiver, NotificationType type, String message, String link,
								  NotificationRefType refType, Long refId) {
		return Notification.builder()
						   .receiver(receiver)
						   .type(type)
						   .message(message)
						   .link(link)
						   .refType(refType != null ? refType : NotificationRefType.SYSTEM)
						   .refId(refId)
						   .build();
	}

	// 읽음 처리용 메서드 도메인
	public void markAsRead() {
		this.isRead = true;
	}
}
