package com.explorer.gabom.domain.sql.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.sql.service.SqlExecutionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/sql")
@RequiredArgsConstructor
public class SqlExecutionController {

	private final SqlExecutionService sqlExecutionService;

	@PostMapping("/run")
	public ResponseEntity<Void> runSqlFile(@RequestParam String file) {
		sqlExecutionService.executeSqlFile(file);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
