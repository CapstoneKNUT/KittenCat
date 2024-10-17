package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.PlanSet;

public interface PlanRepository extends JpaRepository<PlanSet, Long> {

}
