package com.explorer.gabom.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
