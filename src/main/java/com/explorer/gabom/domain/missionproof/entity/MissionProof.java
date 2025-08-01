package com.explorer.gabom.domain.missionproof.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;
import com.explorer.gabom.domain.file.entity.AttachmentFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "mission_proof")
@SQLDelete(sql = "UPDATE mission_proof SET deleted_at = NOW() WHERE id = ?")

public class MissionProof extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@Column(nullable = false)
	private String title;

	@Lob
	private String content;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "mission_proof_id")
	private List<AttachmentFile> imageFiles = new ArrayList<>();

	@Column(nullable = false)
	private Integer starRating;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private Long targetId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MissionProofType fieldType;

	public void update(String title, String content, List<AttachmentFile> newFiles) {
		this.title = title;
		this.content = content;
		// 기존 리스트를 clear 후 addAll로 교체 (orphan 문제 방지)
		this.imageFiles.clear();
		this.imageFiles.addAll(newFiles);
	}


	public void delete() {
	}
}
