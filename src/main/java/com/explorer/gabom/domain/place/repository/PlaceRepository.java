package com.explorer.gabom.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

	Optional<Place> findByIdAndUserId(Long placeId, Long userId);

	Optional<Place> findByIdAndStatusAndDeletedAtIsNull(Long id, PlaceStatus status);
}