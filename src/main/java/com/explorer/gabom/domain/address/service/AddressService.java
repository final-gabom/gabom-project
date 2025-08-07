package com.explorer.gabom.domain.address.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final EupmyeondongRepository emdRepository;

	@Transactional
	public AddressDto createOrReplace(AddressRequest request) {
		if (!emdRepository.existsByEmdCd(request.getEmdCd())) {
			throw new CustomException(ErrorCode.INVALID_ADDRESS_CODE);
		}

		// 기존 주소 삭제
		addressRepository.deleteByAddressTypeCdAndTargetId(
			request.getAddressTypeCd().name(),
			request.getTargetId()
		);

		// 새로운 주소 생성
		String emdCd = request.getEmdCd();
		String sdCd = emdCd.substring(0, 2);
		String sggCd = emdCd.substring(0, 5);

		Address address = Address.builder()
								 .addressTypeCd(request.getAddressTypeCd().name())
								 .targetId(request.getTargetId())
								 .sdCd(sdCd)
								 .sggCd(sggCd)
								 .emdCd(emdCd)
								 .detail(request.getAddressDetail())
								 .lat(request.getLat())
								 .lng(request.getLng())
								 .build();

		return AddressDto.toDto(addressRepository.save(address));
	}
}