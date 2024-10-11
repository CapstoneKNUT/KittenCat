package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

}
