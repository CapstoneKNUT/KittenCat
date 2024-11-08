package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.Place;
import org.zerock.b01.domain.Store;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.repository.PlaceRepository;
import org.zerock.b01.repository.StoreRepository;

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

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    @Override
    public PlaceDTO readOne(Integer pord) {

        Optional<Place> result = placeRepository.findById(pord);

        Place place = result.orElseThrow();

        PlaceDTO placeDTO = entityToDTO(place);

        return placeDTO;
    }
    @Override
    public PageResponseDTO<PlaceDTO> list(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("pord");

        Page<Place> result = placeRepository.findAll(pageable);

        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(place -> modelMapper.map(place,PlaceDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<PlaceDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public Long register(Integer pord, String mid){
        Place place = placeRepository.findById(pord)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        Member member = memberRepository.findById(mid)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Store store = Store.builder()
                .p_name(place.getP_name())
                .p_category(place.getP_category())
                .p_address(place.getP_address())
                .p_content(place.getP_content())
                .mid(member)
                .p_image(place.getP_image())
                .p_call(place.getP_call())
                .p_star(place.getP_star())
                .p_site(place.getP_site())
                .p_opentime(place.getP_opentime())
                .p_park(place.getP_park())
                .build();

        return storeRepository.save(store).getSno();
    }
}
