package com.explorer.gabom.domain.address.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.dto.response.AddressCreateResponse;
import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;
import com.explorer.gabom.domain.address.type.AddressType;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private EupmyeondongRepository emdRepository;

	@InjectMocks
	private AddressService addressService;

	@Test
	@DisplayName("주소 등록 성공: 기존 삭제 후 새로 저장")
	void testCreateOrReplace_success() {
		// given
		AddressRequest request = AddressRequest.builder()
											   .addressTypeCd(AddressType.USER)
											   .targetId(1L)
											   .emdCd("1101050000")
											   .addressDetail("와르르멘션 204호")
											   .lat(37.498095)
											   .lng(127.027610)
											   .build();

		Eupmyeondong emd = Eupmyeondong.builder()
									   .emdCd("1101050000")
									   .emdNm("청운효자동")
									   .build();

		Address savedAddress = Address.builder()
									  .id(100L)
									  .addressTypeCd("USER")
									  .targetId(1L)
									  .sdCd("11")
									  .sggCd("11010")
									  .emdCd("1101050000")
									  .detail("와르르멘션 204호")
									  .lat(37.498095)
									  .lng(127.027610)
									  .build();

		given(emdRepository.findById("1101050000")).willReturn(Optional.of(emd));
		given(addressRepository.save(any(Address.class))).willReturn(savedAddress);

		// when
		AddressCreateResponse result = addressService.createOrReplace(request);

		// then
		assertThat(result.getId()).isEqualTo(100L);
		assertThat(result.getEmdCd()).isEqualTo("1101050000");
		assertThat(result.getEmdNm()).isEqualTo("청운효자동");
		assertThat(result.getLat()).isEqualTo(37.498095);
		assertThat(result.getLng()).isEqualTo(127.027610);

		verify(addressRepository).deleteByAddressTypeCdAndTargetId("USER", 1L);
		verify(addressRepository).save(any(Address.class));
	}

	@Test
	@DisplayName("주소 등록 실패: 존재하지 않는 읍면동 코드")
	void testCreateOrReplace_invalidEmd() {
		// given
		AddressRequest request = AddressRequest.builder()
											   .addressTypeCd(AddressType.USER)
											   .targetId(1L)
											   .emdCd("9999999999") // 없는 코드
											   .addressDetail("어딘가 1-1")
											   .lat(0.0)
											   .lng(0.0)
											   .build();

		given(emdRepository.findById("9999999999")).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> addressService.createOrReplace(request))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ADDRESS_CODE);

		verify(addressRepository, never()).save(any());
	}
}
