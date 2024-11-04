package org.zerock.b01.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.PlanPlace;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlanPlaceRepositoryTest {
    @Autowired
    private PlanPlaceRepository planPlaceRepository;

    @Test
    void findLastPlanPlaceByPlanNo() {
        List<PlanPlace> result = planPlaceRepository.findAllAfterId(2L, 3L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllAfterId() {
    }

    @Test
    void findNextId() {
    }

    @Test
    void findPrevId() {
    }
}