package com.explorer.gabom.domain.quest.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_quest")
@EntityListeners(AuditingEntityListener.class)
public class UserQuest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "quest_id")
	private Quest quest;

	@Enumerated(EnumType.STRING)
	private ProgressStatus progressStatus;

	@Lob
	private String progressData;

	@CreatedDate
	private LocalDateTime completedAt;

	private Integer rewardReceived;

	private Boolean isRewardClaimed;
}
