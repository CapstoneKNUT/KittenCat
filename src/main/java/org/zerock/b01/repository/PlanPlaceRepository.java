package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.PlanPlace;

public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {
    @Query(value = "SELECT * FROM plan_place WHERE plan_no = :planNo ORDER BY pp_ord DESC LIMIT 1", nativeQuery = true)
    PlanPlace findLastPlanPlaceByPlanNo(@Param("planNo") Long planNo);
}
