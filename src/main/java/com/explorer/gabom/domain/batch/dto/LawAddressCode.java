package com.explorer.gabom.domain.batch.dto;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawAddressCode {

	@CsvBindByPosition(position = 0)
	private String code;

	@CsvBindByPosition(position = 1)
	private String name;
}