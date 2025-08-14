package com.explorer.gabom.domain.sql.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqlExecutionService {
	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void executeSqlFile(String filePath) {
		try {
			ClassPathResource resource = new ClassPathResource(filePath);
			String sql = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
				.lines().collect(Collectors.joining("\n"));
			String[] statements = sql.split(";");
			for (String statement : statements) {
				String trimmed = statement.trim();
				if (!trimmed.isEmpty()) {
					log.debug("Executing SQL: {}", trimmed);
					jdbcTemplate.execute(trimmed);
				}
			}
		} catch (Exception e) {
			log.error("SQL execution failed for {}: {}", filePath, e.getMessage(), e);
			throw new CustomException(ErrorCode.SQL_EXECUTION_FAILED);
		}
	}
}
