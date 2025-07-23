package com.explorer.gabom.domain.ranking.entity;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ranking")
public class Ranking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(nullable = false)
	private int rank;

	@Column(nullable = false)
	private int level;

	@Column(nullable = false)
	private int exp;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String titleName;

	@Column(nullable = false)
	private LocalDateTime updatedAt;
}

