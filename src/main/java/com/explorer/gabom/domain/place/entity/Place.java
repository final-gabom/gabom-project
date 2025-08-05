package com.explorer.gabom.domain.place.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "place")
@SQLDelete(sql = "UPDATE place SET deleted_at = NOW() WHERE id = ?")
public class Place extends BaseTimeEntity {

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("orderIdx ASC")
	private final List<PlaceFile> files = new ArrayList<>(); // TODO: 이미지 연동 후 구현 예정

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 255)
	private String address;

	@Column(nullable = false)
	private Double lat;

	@Column(nullable = false)
	private Double lng;

	@Lob
	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String proofMethod;

	@Column(nullable = false)
	private Integer viewCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PlaceStatus status;

	@OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
	private final List<MissionProof> missionProofs = new ArrayList<>();

	public Place(PlaceCreateRequest request, User user) {
		this.user = user;
		this.title = request.getTitle();
		this.address = request.getAddress();
		this.lat = request.getLat();
		this.lng = request.getLng();
		this.proofMethod = request.getProofMethod();
		this.content = request.getContent();
		this.viewCount = 0; // 기본값
	}

	public void approve() {
		this.status = PlaceStatus.APPROVED;
	}

	public void markAsDeleted() {
		this.deletedAt = LocalDateTime.now();
		this.status = PlaceStatus.DELETED;
	}

	public void increaseViewCount() {
		this.viewCount += 1;
	}

	public AttachmentFile getFirstFile() {
		return files.stream()
					.findFirst()
					.map(PlaceFile::getFile)
					.orElse(null);
	}
}
