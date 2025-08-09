package com.explorer.gabom.domain.batch.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.entity.Sido;
import com.explorer.gabom.domain.address.entity.Sigungu;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressUpsertService {
	private final JdbcTemplate jdbc;

	@Transactional
	public void upsertSd(List<Sido> list) {
		if (list.isEmpty())
			return;
		String sql = """
			INSERT INTO sido (sd_cd, sd_nm)
			VALUES (?, ?)
			AS new
			ON DUPLICATE KEY UPDATE
			  sd_nm = new.sd_nm
			""";
		jdbc.batchUpdate(sql, list, list.size(), (ps, sd) -> {
			ps.setString(1, sd.getSdCd());
			ps.setString(2, sd.getSdNm());
		});
	}

	@Transactional
	public void upsertSgg(List<Sigungu> list) {
		if (list.isEmpty())
			return;
		String sql = """
			INSERT INTO sigungu (sgg_cd, sgg_nm, sd_cd)
			VALUES (?, ?, ?)
			AS new
			ON DUPLICATE KEY UPDATE
			  sgg_nm = new.sgg_nm,
			  sd_cd  = new.sd_cd
			""";
		jdbc.batchUpdate(sql, list, list.size(), (ps, s) -> {
			ps.setString(1, s.getSggCd());
			ps.setString(2, s.getSggNm());
			ps.setString(3, s.getSdCd());
		});
	}

	@Transactional
	public void upsertEmd(List<Eupmyeondong> list) {
		if (list.isEmpty())
			return;
		String sql = """
			INSERT INTO eupmyeondong (emd_cd, emd_nm, sgg_cd)
			VALUES (?, ?, ?)
			AS new
			ON DUPLICATE KEY UPDATE
			  emd_nm = new.emd_nm,
			  sgg_cd = new.sgg_cd
			""";
		jdbc.batchUpdate(sql, list, list.size(), (ps, e) -> {
			ps.setString(1, e.getEmdCd());
			ps.setString(2, e.getEmdNm());
			ps.setString(3, e.getSggCd());
		});
	}
}
