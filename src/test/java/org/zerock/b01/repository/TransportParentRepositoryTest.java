package org.zerock.b01.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.TransportParent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TransportParentRepositoryTest {

    @Autowired
    private TransportParentRepository transportParentRepository;

    @Test
    void findLastTransportParent() {
        TransportParent result = transportParentRepository.findLastTransportParent("예찬");
        assertThat(result).isNotNull();
        assertThat(result.getWriter()).isEqualTo("예찬");
    }

    @Test
    void findLastTwoTransportParents() {
    }

    @Test
    void findByPpord() {
    }
}