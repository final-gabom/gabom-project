package com.explorer.gabom.domain.address.entity;

import com.explorer.gabom.domain.address.type.AddressType;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "addressTypeCd", updatable = false, insertable = false)
	private AddressType addressType;
	private String addressTypeCd;

	private Long targetId;

	@Column(nullable = false, length = 2)
	private String sdCd;

	@Column(nullable = false, length = 5)
	private String sggCd;

	/** 연관된 읍·면·동 (읽기 전용 필드) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emdCd", insertable = false, updatable = false)
	private Eupmyeondong eupmyeondong; // 읍·면·동 엔티티

	/** 읍·면·동 코드 (저장 필드) */
	@Column(nullable = false, length = 10)
	private String emdCd;

	private String detail;

	@Column(nullable = false)
	private Double lat;

	@Column(nullable = false)
	private Double lng;

	public void update(String sdCd, String sggCd, String emdCd, String detail, Double lat, Double lng) {
		this.sdCd = sdCd;
		this.sggCd = sggCd;
		this.emdCd = emdCd;
		this.detail = detail;
		this.lat = lat;
		this.lng = lng;
	}
}
