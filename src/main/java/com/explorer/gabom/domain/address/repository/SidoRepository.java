package com.explorer.gabom.domain.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Sido;

public interface SidoRepository extends JpaRepository<Sido, String> {
	Optional<Sido> findBySdNm(String sdNm);
}
