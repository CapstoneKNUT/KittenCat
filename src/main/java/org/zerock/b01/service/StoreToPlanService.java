package org.zerock.b01.service;

import org.zerock.b01.domain.StoreToPlan;
import org.zerock.b01.dto.StoreToPlanDTO;

public interface StoreToPlanService {

    StoreToPlanDTO readOne(Long spNo);

    Long register(Long sno);

    default StoreToPlanDTO entityToDTO(StoreToPlan storeToPlan) {

        StoreToPlanDTO storeToPlanDTO = StoreToPlanDTO.builder()
                .spNo(storeToPlan.getSpNo())
                .p_name(storeToPlan.getP_name())
                .p_category(storeToPlan.getP_category())
                .p_address(storeToPlan.getP_address())
                .p_content(storeToPlan.getP_content())
                .p_image(storeToPlan.getP_image())
                .p_call(storeToPlan.getP_call())
                .p_star(storeToPlan.getP_star())
                .p_site(storeToPlan.getP_site())
                .p_opentime(storeToPlan.getP_opentime())
                .p_park(storeToPlan.getP_park())
                .build();

        return storeToPlanDTO;
    }
}
