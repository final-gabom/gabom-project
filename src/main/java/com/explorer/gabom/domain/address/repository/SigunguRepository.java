package com.explorer.gabom.domain.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, String> {
	Optional<Sigungu> findFirstBySggNmAndSdCd(String sggNm, String sdCd);

	Optional<Sigungu> findFirstBySggNmEndsWithAndSdCd(String sggNm, String sdCd);

	Optional<Sigungu> findFirstBySggNmContainingAndSdCd(String sggNm, String sdCd);
}
