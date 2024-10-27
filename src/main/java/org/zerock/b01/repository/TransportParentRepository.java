package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.TransportParent;

import java.util.List;

public interface TransportParentRepository extends JpaRepository<TransportParent, Long> {

    @Query(value = "SELECT * FROM transportparent WHERE writer = :writer ORDER BY pp_ord DESC LIMIT 1", nativeQuery = true)
    TransportParent findLastTransportParent(@Param("writer") String writer);

    @Query(value = "SELECT * FROM transportparent WHERE writer = :writer ORDER BY pp_ord DESC LIMIT 2", nativeQuery = true)
    List<TransportParent> findLastTwoTransportParents(@Param("writer") String writer);

    @Query(value = "SELECT * FROM transportparent WHERE ppOrd := ppOrd", nativeQuery = true)
    TransportParent findByPpord(@Param("ppOrd") Long ppOrd);

}
