package org.zerock.b01.service;

import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface PlaceService {

    Long register(PlaceDTO placeDTO);

    PlaceDTO readOne(Long bno);

    void modify(PlaceDTO placeDTO);

    void remove(Long bno);

    PageResponseDTO<PlaceDTO> list(PageRequestDTO pageRequestDTO);

    //댓글의 숫자까지 처리
    PageResponseDTO<PlaceListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    //게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDTO<PlaceListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    default Place dtoToEntity(PlaceDTO placeDTO){

        Place place = Place.builder()
                .bno(placeDTO.getBno())
                .title(placeDTO.getTitle())
                .content(placeDTO.getContent())
                .writer(placeDTO.getWriter())

                .build();

        if(placeDTO.getFileNames() != null){
            placeDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                place.addImage(arr[0], arr[1]);
            });
        }
        return place;
    }

    default PlaceDTO entityToDTO(Place place) {

        PlaceDTO placeDTO = PlaceDTO.builder()
                .bno(place.getBno())
                .title(place.getTitle())
                .content(place.getContent())
                .writer(place.getWriter())
                .regDate(place.getRegDate())
                .modDate(place.getModDate())
                .build();

        List<String> fileNames =
        place.getImageSet().stream().sorted().map(placeImage ->
                placeImage.getUuid()+"_"+placeImage.getFileName()).collect(Collectors.toList());

        placeDTO.setFileNames(fileNames);

        return placeDTO;
    }

}
