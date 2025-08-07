package com.explorer.gabom.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "eupmyeondong")
public class Eupmyeondong {

	/** 읍면동 코드 (PK) */
	@Id
	@Column(length = 5)
	private String emdCd;

	/** 연관된 시군구 (FK → sigungu.sgg_cd) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sggCd", nullable = false, updatable = false, insertable = false)
	private Sigungu sigungu;
	@Column(nullable = false)
	private String sggCd;

	/** 읍면동명 */
	@Column(length = 100, nullable = false)
	private String emdNm;
}
