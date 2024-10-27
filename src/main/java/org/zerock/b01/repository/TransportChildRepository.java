package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.TransportChild;

import java.util.List;

public interface TransportChildRepository extends JpaRepository<TransportChild, Long> {
    @Query(value = "SELECT * FROM transportchild WHERE tno = :tno", nativeQuery = true)
    List<TransportChild> findByTno(@Param("tno") Long tno);
}
