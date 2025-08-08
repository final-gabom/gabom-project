package com.explorer.gabom.domain.ranking.entity;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.title.entity.Title;

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

	@ManyToOne
	@JoinColumn(name = "title_id")
	private Title title;

	@ManyToOne
	@JoinColumn(name = "profile_image_id")
	private AttachmentFile profileImage;

	public Ranking(Long userId, Integer rankNo, int level, int exp, String nickname, Title title,
				   AttachmentFile profileImage) {
		this.userId = userId;
		this.rankNo = rankNo;
		this.level = level;
		this.exp = exp;
		this.nickname = nickname;
		this.title = title;
		this.profileImage = profileImage;
	}

	public void update(Integer rankNo, int exp, int level, String nickname, Title title,
					   AttachmentFile profileImage) {
		this.rankNo = rankNo;
		this.exp = exp;
		this.level = level;
		this.nickname = nickname;
		this.title = title;
		this.profileImage = profileImage;
	}
}

