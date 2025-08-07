package com.explorer.gabom.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 연관된 시도 (FK → sido.ctpv_cd) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sdCd", insertable = false, updatable = false)
	private Sido sido;
	@Column(nullable = false)
	private String sdCd;

	/** 연관된 시군구 (FK → sigungu.sgg_cd) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sggCd", insertable = false, updatable = false)
	private Sigungu sigungu;
	@Column(nullable = false)
	private String sggCd;

	/** 연관된 읍면동 (FK → eupmyeondong.emd_cd) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emdCd", insertable = false, updatable = false)
	private Eupmyeondong eupmyeondong;
	@Column(nullable = false)
	private String emdCd;

	/** 상세주소 */
	private String street;

	/** 위도 */
	@Column(nullable = false)
	private Double latitude;

	/** 경도 */
	@Column(nullable = false)
	private Double longitude;
}
