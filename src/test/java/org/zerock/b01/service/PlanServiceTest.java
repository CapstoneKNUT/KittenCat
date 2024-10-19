package org.zerock.b01.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.Search.GetXYRequest;

@SpringBootTest
class PlanServiceTest {

    @Autowired
    private PlanService planService;

    @Test
    public void getXY() {
        var search = new GetXYRequest();
        search.setQuery("갈비집");

        var result = planService.getXY(search);
        System.out.println(result);
    }
}