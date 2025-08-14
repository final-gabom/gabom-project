package com.explorer.gabom.domain.address.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.dto.AddressCodeComponents;
import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;
import com.explorer.gabom.domain.address.type.AddressType;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final EupmyeondongRepository emdRepository;

	@Transactional
	public AddressDto createOrReplace(AddressRequest request) {
		AddressCodeComponents codes = parseAddressCodes(request.getEmdCd());

		Eupmyeondong emd = emdRepository.findById(codes.getEmdCd())
										.orElseThrow(() -> new CustomException(ErrorCode.INVALID_ADDRESS_CODE));

		addressRepository.deleteByAddressTypeCdAndTargetId(
			request.getAddressTypeCd().name(),
			request.getTargetId()
		);

		Address address = Address.builder()
								 .addressTypeCd(request.getAddressTypeCd().name())
								 .targetId(request.getTargetId())
								 .emdCd(codes.getEmdCd())
								 .sggCd(codes.getSggCd())
								 .sdCd(codes.getSdCd())
								 .detail(request.getAddressDetail())
								 .lat(request.getLat())
								 .lng(request.getLng())
								 .build();

		return AddressDto.toDto(addressRepository.save(address), emd);
	}

	private AddressCodeComponents parseAddressCodes(String emdCd) {
		if (emdCd == null || emdCd.length() < 5) {
			throw new CustomException(ErrorCode.INVALID_ADDRESS_CODE);
		}

		String sdCd = emdCd.substring(0, 2);
		String sggCd = emdCd.substring(0, 5);

		return new AddressCodeComponents(sdCd, sggCd, emdCd);
	}

	@Transactional(readOnly = true)
	public AddressDto getByTypeAndTargetId(AddressType addressType, Long id) {
		Address address = addressRepository.findByAddressTypeCdAndTargetId(
			addressType.name(),
			id
		).orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

		return AddressDto.toDto(address);
	}
}