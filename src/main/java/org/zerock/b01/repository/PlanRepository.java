package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;

public interface PlanRepository extends JpaRepository<PlanSet, Long> {
    @Query(value = "SELECT * FROM plan_set WHERE writer = :writer ORDER BY plan_no DESC LIMIT 1", nativeQuery = true)
    PlanSet findLastPlanSetbyWriter(@Param("writer") String writer);

}