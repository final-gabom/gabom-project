package com.explorer.gabom.domain.ranking.entity;

import com.explorer.gabom.domain.file.entity.AttachmentFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@Column(nullable = true)
	private Integer rankNo;

	@Column(nullable = false)
	private int level;

	@Column(nullable = false)
	private int exp;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String titleName;

	@ManyToOne
	@JoinColumn(name = "profileImageId", insertable = false, updatable = false)
	private AttachmentFile profileImage;

	@Column(nullable = true)
	private String profileImageId;

	public Ranking(Long userId, Integer rankNo, int level, int exp, String nickname, String titleName,
				   String profileImageId) {
		this.userId = userId;
		this.rankNo = rankNo;
		this.level = level;
		this.exp = exp;
		this.nickname = nickname;
		this.titleName = titleName;
		this.profileImageId = profileImageId;
	}

	public void update(int exp, int level, String nickname, String titleName, String profileImageId) {
		this.exp = exp;
		this.level = level;
		this.nickname = nickname;
		this.titleName = titleName;
		this.profileImageId = profileImageId;
	}
}

