package com.explorer.gabom.domain.activity.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.explorer.gabom.domain.activity.type.ActivityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_log")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 활동 주체
	private Long userId;

	// 타겟 ID (예: 인증글 ID, 장소 ID 등)
	private Long targetId;

	// 어떤 종류의 활동인지 (ex: 인증글 작성, 좋아요 등)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ActivityType activityType;

	@Lob
	private String description;

	@Column(length = 15)
	private String ipAddress;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public ActivityLog(Long userId, Long targetId, ActivityType activityType, String description, String ipAddress) {
		this.userId = userId;
		this.targetId = targetId;
		this.activityType = activityType;
		this.description = description;
		this.ipAddress = ipAddress;
	}
}

