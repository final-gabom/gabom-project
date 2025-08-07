package com.explorer.gabom.domain.notification.entity;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.notification.type.NotificationType;
import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	private boolean isRead; // 읽음 여부 (true면 읽음, false면 안 읽음)

	private LocalDateTime createdAt; // 알림 생성 시각

	// 알림 객체 생성용 static 팩토리 메서드
	public static Notification of(User receiver, NotificationType type, String message, String link) {
		Notification notification = new Notification();
		notification.receiver = receiver;
		notification.type = type;
		notification.message = message;
		notification.link = link;
		notification.isRead = false; // 기본값은 '읽지 않음'
		notification.createdAt = LocalDateTime.now(); // 현재 시간 기준으로 생성
		return notification;
	}

	// 읽음 처리용 도메인 메서드
	public void markAsRead() {
		this.isRead = true;
	}
}
