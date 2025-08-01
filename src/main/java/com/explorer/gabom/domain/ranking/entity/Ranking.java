package com.explorer.gabom.domain.ranking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ranking")
@Getter
@NoArgsConstructor
public class Ranking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long userId;

	@Column(nullable = false)
	private int rankNo;

	@Column(nullable = false)
	private int level;

	@Column(nullable = false)
	private int exp;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String titleName;

	@Column(nullable = true)
	private String profileImageUrl;

	public Ranking(Long userId, int rankOrder, int level, int exp, String nickname, String titleName,
				   String profileImageUrl) {
		this.userId = userId;
		this.rankNo = rankOrder;
		this.level = level;
		this.exp = exp;
		this.nickname = nickname;
		this.titleName = titleName;
		this.profileImageUrl = profileImageUrl;
	}
}

