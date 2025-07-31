package com.explorer.gabom.domain.quest.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_quest")
@Getter
@NoArgsConstructor
@SQLRestriction("deleted = false")
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
	private ProgressStatus progressStatus = ProgressStatus.IN_PROGRESS;

	private int progressCount = 0;

	@Lob
	private String progressData;

	private LocalDateTime completedAt;

	private boolean rewardClaimed = false;

	@Column(nullable = false)
	private boolean deleted = false;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public UserQuest(User user, Quest quest) {
		this.user = user;
		this.quest = quest;
		this.progressCount = 0;
		this.rewardClaimed = false;
	}

	public void increaseProgress(int step) {
		if (!isCompleted()) {
			this.progressCount += step;
			if (this.progressCount >= quest.getAcquireCondition()) {
				this.progressStatus = ProgressStatus.COMPLETED;
				this.completedAt = LocalDateTime.now();
			}
		}
	}

	public boolean isCompleted() {
		return this.progressStatus == ProgressStatus.COMPLETED;
	}

	public void markRewardClaimed() {
		this.rewardClaimed = true;
	}

	public void markCompleted() {
		this.progressStatus = ProgressStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}

	public void markInProgress() {
		this.progressStatus = ProgressStatus.IN_PROGRESS;
		this.completedAt = null;
	}

	public void markAsDeleted() {
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
	}
}
