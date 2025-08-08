package com.explorer.gabom.domain.address.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.address.dto.AddressCodeComponents;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.dto.response.AddressCreateResponse;
import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.entity.Eupmyeondong;
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
	public AddressCreateResponse createOrReplace(AddressRequest request) {
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
								 .sdCd(codes.getSdCd())
								 .sggCd(codes.getSggCd())
								 .emdCd(codes.getEmdCd())
								 .detail(request.getAddressDetail())
								 .lat(request.getLat())
								 .lng(request.getLng())
								 .build();

		return AddressCreateResponse.toDto(addressRepository.save(address), emd);
	}

	private AddressCodeComponents parseAddressCodes(String emdCd) {
		if (emdCd == null || emdCd.length() < 5) {
			throw new CustomException(ErrorCode.INVALID_ADDRESS_CODE);
		}

		String sdCd = emdCd.substring(0, 2);
		String sggCd = emdCd.substring(0, 5);

		return new AddressCodeComponents(sdCd, sggCd, emdCd);
	}
}