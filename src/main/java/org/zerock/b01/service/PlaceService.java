package org.zerock.b01.service;

import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.*;

public interface PlaceService {

    PlaceDTO readOne(Integer pord);

    PageResponseDTO<PlaceDTO> list(PageRequestDTO pageRequestDTO);

    Long register(Integer pord, String mid);

    default PlaceDTO entityToDTO(Place place) {

        PlaceDTO placeDTO = PlaceDTO.builder()
                .pord(place.getPord())
                .p_name(place.getP_name())
                .p_category(place.getP_category())
                .p_address(place.getP_address())
                .p_content(place.getP_content())
                .p_image(place.getP_image())
                .p_call(place.getP_call())
                .p_star(place.getP_star())
                .p_site(place.getP_site())
                .p_opentime(place.getP_opentime())
                .p_park(place.getP_park())
                .build();

        return placeDTO;
    }
}
