package com.explorer.gabom.domain.place.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "places")
@SQLDelete(sql = "UPDATE place SET deleted_at = NOW() WHERE id = ?")
public class Place extends BaseTimeEntity {

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

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("orderIdx ASC")
	private List<PlaceFile> files = new ArrayList<>(); // TODO: 이미지 연동 후 구현 예정

	@Builder
	public Place(User user, String title, String address, Double lat, Double lng,
				 String content, String proofMethod, Integer viewCount) {
		this.user = user;
		this.title = title;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.content = content;
		this.proofMethod = proofMethod;
		this.viewCount = viewCount;
	}

}
