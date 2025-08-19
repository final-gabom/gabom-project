package com.explorer.gabom.domain.exploration.entity;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Exploration extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	private Place place;

	private int rewardPoint;
	private int rewardExp;

	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public enum Status {
		IN_PROGRESS, COMPLETED, EXPIRED, CANCELED
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Status status = Status.IN_PROGRESS;

	@Column(nullable = false)
	@Builder.Default
	private boolean almostNotified = false;

	@Column(nullable = false)
	@Builder.Default
	private boolean expiredNotified = false;


	public Exploration(Long id, User user, Place place, int rewardPoint, int rewardExp,
					   LocalDateTime startAt, LocalDateTime endAt) {
		this.id = id;
		this.user = user;
		this.place = place;
		this.rewardPoint = rewardPoint;
		this.rewardExp = rewardExp;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	// 시간 운영 기준(3시간)으로 고정
	public void extendDeadline() {
		this.endAt = this.endAt.plusMinutes(3);
	}

	public boolean isActive() {
		return status == Status.IN_PROGRESS;
	}

	public void markCompleted() {
		this.status = Status.COMPLETED;
	}

	public void markExpired() {
		this.status = Status.EXPIRED;
	}

	public void markCanceled() {
		this.status = Status.CANCELED;
	}

	public void markAlmostNotified() {
		this.almostNotified = true;
	}

	public void markExpiredNotified() {
		this.expiredNotified = true;
	}

	public void markInProgress() {
		this.status = Status.IN_PROGRESS;
	}
}
