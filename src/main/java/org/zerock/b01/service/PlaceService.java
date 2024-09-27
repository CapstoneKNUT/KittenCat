package org.zerock.b01.service;

import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface PlaceService {

    Integer register(PlaceDTO placeDTO);

    PlaceDTO readOne(Integer p_ord);

    List<PlaceListAllDTO> list();

    default Place dtoToEntity(PlaceDTO placeDTO){

        Place place = Place.builder()
                .p_ord(placeDTO.getP_ord())
                .p_name(placeDTO.getP_name())
                .p_category(placeDTO.getP_category())
                .p_address(placeDTO.getP_address())
                .p_content(placeDTO.getP_content())
                .bookmark(placeDTO.getBookmark())
                .p_image(placeDTO.getP_image())
                .p_call(placeDTO.getP_call())
                .p_star(placeDTO.getP_star())
                .p_site(placeDTO.getP_site())
                .build();
        return place;
    }

    default PlaceDTO entityToDTO(Place place) {

        PlaceDTO placeDTO = PlaceDTO.builder()
                .p_ord(place.getP_ord())
                .p_name(place.getP_name())
                .p_category(place.getP_category())
                .p_address(place.getP_address())
                .p_content(place.getP_content())
                .bookmark(place.getBookmark())
                .p_image(place.getP_image())
                .p_call(place.getP_call())
                .p_star(place.getP_star())
                .p_site(place.getP_site())
                .build();

        return placeDTO;
    }
}
