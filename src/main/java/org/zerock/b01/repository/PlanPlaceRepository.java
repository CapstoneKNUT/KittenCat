package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.dto.DateRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {

    @Query(value = "SELECT * FROM plan_place WHERE plan_no = :planNo ORDER BY pp_ord DESC LIMIT 1", nativeQuery = true)
    PlanPlace findLastPlanPlaceByPlanNo(@Param("planNo") Long planNo);

    @Query(value = "SELECT * FROM plan_place WHERE plan_no = :planNo AND pp_ord > :ppOrd ORDER BY pp_ord ASC", nativeQuery = true)
    List<PlanPlace> findAllAfterId(@Param("planNo") Long planNo, @Param("ppOrd") Long ppOrd);

    @Query(value = "SELECT * FROM plan_place WHERE plan_no = :planNo AND pp_ord > :ppOrd ORDER BY pp_ord ASC LIMIT 1", nativeQuery = true)
    PlanPlace findNextId(@Param("planNo") Long planNo, @Param("ppOrd") Long ppOrd);

    @Query(value = "SELECT * FROM plan_place WHERE plan_no = :planNo AND pp_ord < :ppOrd ORDER BY pp_ord DESC LIMIT 1", nativeQuery = true)
    PlanPlace findPrevId(@Param("planNo") Long planNo, @Param("ppOrd") Long ppOrd);

}
