package com.explorer.gabom.domain.place.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	Page<Place> findByDeletedAtIsNullAndTitleContainingOrDeletedAtIsNullAndAddressContaining(
		String titleQuery,
		String addressQuery,
		Pageable pageable
	);

	Optional<Place> findByIdAndDeletedAtIsNull(Long id);

}
