package com.explorer.gabom.domain.batch.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.batch.dto.AddressCsv;
import com.explorer.gabom.domain.batch.dto.PlaceCsv;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvImporter {

	private static final String LAW_CODES_CSV_PATH = "classpath:data/emd_codes.csv";

	private final ResourceLoader resourceLoader;

	public List<AddressCsv> readCsv() throws Exception {
		Resource resource = resourceLoader.getResource(LAW_CODES_CSV_PATH);
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return new CsvToBeanBuilder<AddressCsv>(reader)
				.withType(AddressCsv.class)
				.withIgnoreLeadingWhiteSpace(true)
				.withSeparator(',')
				.build()
				.parse();
		}
	}

	public List<PlaceCsv> readPlaceCsv(String url) throws Exception {
		Resource resource = resourceLoader.getResource(url);
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return new CsvToBeanBuilder<PlaceCsv>(reader)
				.withType(PlaceCsv.class)
				.withIgnoreLeadingWhiteSpace(true)
				.withSeparator(',')
				.build()
				.parse();
		}
	}
}
