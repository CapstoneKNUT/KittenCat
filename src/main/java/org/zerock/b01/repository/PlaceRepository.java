package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Place;
import org.zerock.b01.repository.search.PlaceSearch;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceSearch {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Place b where b.bno =:bno")
    Optional<Place> findByIdWithImages(@Param("bno")Long bno);
}
