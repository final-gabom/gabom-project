package com.explorer.gabom.domain.batch.dto;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCsv {
	@CsvBindByName(column = "id", required = false)
	private Long id;

	@CsvBindByName(column = "title")
	private String title;

	@CsvBindByName(column = "address_detail")
	private String address;

	@CsvBindByName(column = "lat")
	private Double lat;

	@CsvBindByName(column = "lng")
	private Double lng;

	@CsvBindByName(column = "content")
	private String content;

	@CsvBindByName(column = "proof_method")
	private String proofMethod;

	@CsvBindByName(column = "view_count", required = false)
	private Integer viewCount;

	@CsvBindByName(column = "status")
	private String status;

	// 있으면 그대로 사용, 없으면 address에서 추출해서 매핑
	@CsvBindByName(column = "emd_cd", required = false)
	private String emdCd;
}


