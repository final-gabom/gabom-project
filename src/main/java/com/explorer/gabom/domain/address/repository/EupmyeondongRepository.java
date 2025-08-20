package com.explorer.gabom.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;

public interface EupmyeondongRepository extends JpaRepository<Eupmyeondong, String> {
	boolean existsByEmdCd(String emdCd);

	@Query("select e.emdCd from Eupmyeondong e")
	List<String> findAllEmdCd();
}
