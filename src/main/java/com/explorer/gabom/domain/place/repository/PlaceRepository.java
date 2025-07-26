package com.explorer.gabom.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

	Optional<Place> findByIdAndUserId(Long placeId, Long userId);
}