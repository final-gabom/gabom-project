package com.explorer.gabom.domain.batch.util;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressRefResolver {

	private final EupmyeondongRepository eupRepo;

	@Transactional(readOnly = true)
	public Eupmyeondong byEmdCd(String emdCd) {
		if (emdCd == null || emdCd.isBlank()) {
			throw new IllegalArgumentException("emdCd 누락");
		}
		return eupRepo.findById(emdCd.trim())
					  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 emdCd: " + emdCd));
	}
}


