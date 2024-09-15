package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.PlaceRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PlaceServiceImpl implements PlaceService{

    private final ModelMapper modelMapper;

    private final PlaceRepository placeRepository;

    @Override
    public Long register(PlaceDTO placeDTO) {

        Place place = dtoToEntity(placeDTO);

        Long bno = placeRepository.save(place).getBno();

        return bno;
    }

    @Override
    public PlaceDTO readOne(Long bno) {

        //place_image까지 조인 처리되는 findByWithImages()를 이용
        Optional<Place> result = placeRepository.findByIdWithImages(bno);

        Place place = result.orElseThrow();

        PlaceDTO placeDTO = entityToDTO(place);

        return placeDTO;
    }

    @Override
    public void modify(PlaceDTO placeDTO) {

        Optional<Place> result = placeRepository.findById(placeDTO.getBno());

        Place place = result.orElseThrow();

        place.change(placeDTO.getTitle(), placeDTO.getContent());

        //첨부파일의 처리
        place.clearImages();

        if(placeDTO.getFileNames() != null){
            for (String fileName : placeDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                place.addImage(arr[0], arr[1]);
            }
        }

        placeRepository.save(place);

    }

    @Override
    public void remove(Long bno) {

        placeRepository.deleteById(bno);

    }

    @Override
    public PageResponseDTO<PlaceDTO> list(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<Place> result = placeRepository.searchAll(types, keyword, pageable);

        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(place -> modelMapper.map(place,PlaceDTO.class)).collect(Collectors.toList());


        return PageResponseDTO.<PlaceDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public PageResponseDTO<PlaceListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<PlaceListReplyCountDTO> result = placeRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDTO.<PlaceListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<PlaceListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<PlaceListAllDTO> result = placeRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<PlaceListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


}
