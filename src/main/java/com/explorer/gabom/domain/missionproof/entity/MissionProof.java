package com.explorer.gabom.domain.missionproof.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;
import com.explorer.gabom.global.file.entity.AttachmentFile;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionProof extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 인증 작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// PLACE or EVENT
	@Column(nullable = false)
	private String fieldType;

	// 대상 ID (장소 ID or 이벤트 ID)
	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	// 이미지 리스트
	@ElementCollection
	@CollectionTable(name = "mission_proof_images", joinColumns = @JoinColumn(name = "mission_proof_id"))
	@Column(name = "image_url")
	private List<String> imageUrls;

	@Column(nullable = false)
	private Integer starRating;

}
