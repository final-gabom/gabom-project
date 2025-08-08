package com.explorer.gabom.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;

public interface EupmyeondongRepository extends JpaRepository<Eupmyeondong, String> {
	boolean existsByEmdCd(String emdCd);
}
