package com.explorer.gabom.domain.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;

public interface EupmyeondongRepository extends JpaRepository<Eupmyeondong, String> {
	boolean existsByEmdCd(String emdCd);

	Optional<Eupmyeondong> findByEmdNmAndSggCd(String emdNm, String sggCd);

	// prefix 매칭 (보조 수단)
	Optional<Eupmyeondong> findFirstByEmdNmStartsWithAndSggCd(String emdNm, String sggCd);
}
