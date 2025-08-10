package com.explorer.gabom.domain.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	void deleteByAddressTypeCdAndTargetId(String addressTypeCd, Long targetId);

	Optional<Address> findByAddressTypeCdAndTargetId(String name, Long id);
}