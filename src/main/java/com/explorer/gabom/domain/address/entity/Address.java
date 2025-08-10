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

	/** 연관된 읍면동 (FK → eupmyeondong.emd_cd) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emdCd", insertable = false, updatable = false)
	private Eupmyeondong eupmyeondong;
	@Column(nullable = false)
	private String emdCd;

	/** 상세주소 */
	private String detail;

	/** 위도 */
	@Column(nullable = false)
	private Double lat;

	/** 경도 */
	@Column(nullable = false)
	private Double lng;

	public void update(String emdCd, String detail, Double lat, Double lng) {
		this.emdCd = emdCd;
		this.detail = detail;
		this.lat = lat;
		this.lng = lng;
	}
}
