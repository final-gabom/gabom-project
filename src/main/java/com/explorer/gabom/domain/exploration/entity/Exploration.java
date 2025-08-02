package com.explorer.gabom.domain.exploration.entity;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

	@Builder
	public Exploration(User user, Place place, int rewardPoint, int rewardExp,
					   LocalDateTime startAt, LocalDateTime endAt) {
		this.user = user;
		this.place = place;
		this.rewardPoint = rewardPoint;
		this.rewardExp = rewardExp;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	public void extendDeadline() {
		this.endAt = this.endAt.plusHours(3);
	}
}
