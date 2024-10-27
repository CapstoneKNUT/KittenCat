package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Store;
import org.zerock.b01.domain.StoreToPlan;
import org.zerock.b01.dto.StoreToPlanDTO;
import org.zerock.b01.repository.StoreRepository;
import org.zerock.b01.repository.StoreToPlanRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class StoreToPlanServiceImpl implements StoreToPlanService{

    private final StoreToPlanRepository storeToPlanRepository;

    private final StoreRepository storeRepository;

    @Override
    public StoreToPlanDTO readOne(Long spNo) {

        Optional<StoreToPlan> result = storeToPlanRepository.findById(spNo);

        StoreToPlan StoreToPlan = result.orElseThrow();

        StoreToPlanDTO StoreToPlanDTO = entityToDTO(StoreToPlan);

        return StoreToPlanDTO;
    }

    @Override
    public Long register(Long sno){
        Store store = storeRepository.findById(sno)
                .orElseThrow(() -> new RuntimeException("StoreToPlan not found"));

        StoreToPlan storeToPlan = StoreToPlan.builder()
                .p_name(store.getP_name())
                .p_category(store.getP_category())
                .p_address(store.getP_address())
                .p_content(store.getP_content())
                .p_image(store.getP_image())
                .p_call(store.getP_call())
                .p_star(store.getP_star())
                .p_site(store.getP_site())
                .p_opentime(store.getP_opentime())
                .p_park(store.getP_park())
                .build();

        Long spNo = storeToPlanRepository.save(storeToPlan).getSpNo();

        return spNo;
    }
}
