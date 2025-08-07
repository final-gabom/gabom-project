package com.explorer.gabom.domain.address.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.CreateAddressRequest;
import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.repository.AddressRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;

	@Transactional
	public AddressDto createAddress(CreateAddressRequest req) {

		String emdCd = req.getAddressCd();
		String sdCd  = emdCd.substring(0, 2);
		String sggCd = emdCd.substring(0, 5);

		Address address = Address.builder()
								 .sdCd(sdCd)
								 .sggCd(sggCd)
								 .emdCd(emdCd)
								 .detail(req.getAddressDetail())
								 .lat(req.getLat())
								 .lng(req.getLng())
								 .build();

		return AddressDto.toDto(addressRepository.save(address));
	}
}
