package com.explorer.gabom.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressCodeComponents {
	private final String sdCd;
	private final String sggCd;
	private final String emdCd;
}