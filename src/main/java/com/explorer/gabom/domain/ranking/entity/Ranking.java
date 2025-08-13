package com.explorer.gabom.domain.ranking.entity;

import com.explorer.gabom.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "ranking",
	indexes = {
		@Index(name = "idx_exp_id", columnList = "exp DESC, id ASC")
	}
)
@Getter
@NoArgsConstructor
public class Ranking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column
	private Integer rankNo;

	@Column(nullable = false)
	private int exp;

	public Ranking(User user, Integer rankNo, int exp) {
		this.user = user;
		this.rankNo = rankNo;
		this.exp = exp;
	}

	public void updateExp(int exp) {
		this.exp = exp;
	}

	public void updateRankNo(Integer rankNo) {
		this.rankNo = rankNo;
	}
}
