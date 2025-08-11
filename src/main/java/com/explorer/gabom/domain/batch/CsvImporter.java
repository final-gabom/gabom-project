package com.explorer.gabom.domain.batch;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.batch.dto.LawAddressCode;
import com.explorer.gabom.domain.batch.dto.PlaceCsvRow;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvImporter {

	private static final String PLACE_CSV_PATH = "classpath:data/place_import.csv";
	private static final String LAW_CODES_CSV_PATH = "classpath:data/law_codes.csv";

	private final ResourceLoader resourceLoader;

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

	public List<PlaceCsvRow> readPlaceCsv() throws Exception {
		Resource resource = resourceLoader.getResource(PLACE_CSV_PATH);
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return new CsvToBeanBuilder<PlaceCsvRow>(reader)
				.withType(PlaceCsvRow.class)
				.withIgnoreLeadingWhiteSpace(true)
				.withSeparator(',')
				.build()
				.parse();
		}
	}
}
