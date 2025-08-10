package com.explorer.gabom.domain.batch;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.batch.dto.LawAddressCode;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvImporter {

	private final ResourceLoader resourceLoader;

	private final String LAW_CODES_CSV_PATH = "classpath:data/law_codes.csv";

	public List<LawAddressCode> readCsv() throws Exception {
		Resource resource = resourceLoader.getResource(LAW_CODES_CSV_PATH);
		try (Reader reader = new InputStreamReader(resource.getInputStream())) {
			return new CsvToBeanBuilder<LawAddressCode>(reader)
				.withType(LawAddressCode.class)
				.withIgnoreLeadingWhiteSpace(true)
				.withSeparator(',')
				.build()
				.parse();
		}
	}
}
