package com.explorer.gabom.domain.missionproof.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;


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
@Table(name = "mission_proof")
@SQLDelete(sql = "UPDATE mission_proof SET deleted_at = NOW() WHERE id = ?")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	// 이미지 리스트
	@ElementCollection
	@CollectionTable(name = "mission_proof_images", joinColumns = @JoinColumn(name = "mission_proof_id"))
	@Column(name = "image_url")
	private List<String> imageUrls;


	@Column(nullable = false)
	private Integer starRating;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private String fieldType; // 또는 Enum이면 @Enumerated 붙이기



}
