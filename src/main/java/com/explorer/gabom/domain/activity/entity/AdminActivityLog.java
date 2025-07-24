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
@Table(name = "admin_activity_log")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdminActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long adminId;
	private Long targetId;

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

	public AdminActivityLog(Long adminId, Long targetId, ActivityType activityType, String description,
							String ipAddress) {
		this.adminId = adminId;
		this.targetId = targetId;
		this.activityType = activityType;
		this.description = description;
		this.ipAddress = ipAddress;
	}
}
