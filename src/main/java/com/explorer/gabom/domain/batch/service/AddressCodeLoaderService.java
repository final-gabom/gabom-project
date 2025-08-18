package com.explorer.gabom.domain.batch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.entity.Sido;
import com.explorer.gabom.domain.address.entity.Sigungu;
import com.explorer.gabom.domain.batch.dto.AddressCsv;
import com.explorer.gabom.domain.batch.util.AddressCodeUtils;
import com.explorer.gabom.domain.batch.util.CsvImporter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCodeLoaderService {

	private final CsvImporter csvImporter;

	private final AddressUpsertService upsertService;

	@Transactional
	public void loadFromClasspath() throws Exception {
		List<AddressCsv> rows = csvImporter.readCsv();

		Map<String, Sido> sdMap = new HashMap<>();
		Map<String, Sigungu> sggMap = new HashMap<>();
		Map<String, Eupmyeondong> emdMap = new HashMap<>();

		for (AddressCsv row : rows) {
			if (row == null)
				continue;

			String code = row.getCode();
			String name = row.getName();

			code = code.trim();
			name = name.trim();

			if (!AddressCodeUtils.isValidLawCode(code))
				continue;

			String sdCd = AddressCodeUtils.sdCd(code);
			String sggCd = AddressCodeUtils.sggCd(code);

			// 시도
			sdMap.put(sdCd, Sido.builder()
								.sdCd(sdCd)
								.sdNm(AddressCodeUtils.sdNm(name))
								.build());

			// 시군구
			sggMap.put(sggCd, Sigungu.builder()
									 .sggCd(sggCd)
									 .sggNm(name)
									 .sdCd(sdCd)
									 .build());

			// 읍면동
			emdMap.put(code, Eupmyeondong.builder()
										 .emdCd(code)
										 .emdNm(name)
										 .sggCd(sggCd)
										 .build());
		}
		var sdList = new ArrayList<>(sdMap.values());
		var sggList = new ArrayList<>(sggMap.values());
		var emdList = new ArrayList<>(emdMap.values());

		log.info("batch sizes: sd={}, sgg={}, emd={}", sdList.size(), sggList.size(), emdList.size());

		upsertService.upsertSd(sdList);
		upsertService.upsertSgg(sggList);
		upsertService.upsertEmd(emdList);
	}
}