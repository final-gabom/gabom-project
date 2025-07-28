package com.explorer.gabom.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

}
