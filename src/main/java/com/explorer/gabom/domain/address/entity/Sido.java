package com.explorer.gabom.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "sido")
public class Sido {

	@Id
	@Column(length = 2)
	private String sdCd;

	@Column(length = 100, nullable = false)
	private String sdNm;
}
