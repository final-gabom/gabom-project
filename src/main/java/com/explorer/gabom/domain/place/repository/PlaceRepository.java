package com.explorer.gabom.domain.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

	Optional<Place> findByIdAndStatusInAndDeletedAtIsNull(Long id, List<PlaceStatus> status);

	Optional<Place> findByIdAndStatus(Long placeId, PlaceStatus status);
}