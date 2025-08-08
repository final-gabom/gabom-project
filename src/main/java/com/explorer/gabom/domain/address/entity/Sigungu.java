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
@Table(name = "sigungu")
public class Sigungu {

	/** 시군구 코드 (PK) */
	@Id
	@Column(length = 5)
	private String sggCd;

	/** 연관된 시도 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sdCd", nullable = false, updatable = false, insertable = false)
	private Sido sido;
	@Column(nullable = false)
	private String sdCd;

	/** 시군구명 */
	@Column(length = 100, nullable = false)
	private String sggNm;
}
