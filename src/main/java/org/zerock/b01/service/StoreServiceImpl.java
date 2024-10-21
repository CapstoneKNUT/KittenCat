package org.zerock.b01.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Store;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;
import org.zerock.b01.repository.StoreRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository,  ModelMapper modelMapper) {
        this.storeRepository = storeRepository;
        this.modelMapper = modelMapper;
    }

    // 페이지로 나타내기
    @Override
    public PageResponseDTO<StoreDTO> list(String username,PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("sno"); // "sno" 기준으로 정렬
        Page<Store> result = storeRepository.findByMid_Mid(username, pageable);

        List<StoreDTO> dtoList = result.getContent().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    // 상세페이지로 이동
    @Override
    public StoreDTO readOne(String username, Long sno) {
        Optional<Store> result = storeRepository.findByMid_MidAndSno(username,sno); // 인스턴스 변수 사용

        Store store = result.orElseThrow();

        // 엔티티를 DTO로 변환 후 반환
        StoreDTO storeDTO = entityToDTO(store);

        return storeDTO;
    }

    // 제거하기
    @Override
    @Transactional
    public void remove(String username, Long sno) {
        storeRepository.deleteByMid_MidAndSno(username,sno);
    }

    // 여행지 이름 검색하여 찜목록 조회
    public PageResponseDTO<StoreDTO> searchBookmarks(String username, String p_name, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("sno");
        Page<Store> result = storeRepository.findByUsernameAndPlaceNameContaining(username, p_name,pageable);

        List<StoreDTO> dtoList = result.getContent().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();


    }




    // 엔티티를 DTO로 변환
    private StoreDTO entityToDTO(Store store) {
        return modelMapper.map(store, StoreDTO.class);
    }


    // DTO를 엔티티로 변환
    protected Store dtoToEntity(StoreDTO storeDTO) {
        return modelMapper.map(storeDTO, Store.class);
    }


}
    //    // 추가하기
//    @Override
//    public String register(StoreDTO storeDTO) {
//        // StoreDTO의 bookmark에서 Member 정보를 가져옴
//        String memberId = storeDTO.getBookmark();
//        memberService.read(memberId);  // Member가 존재하는지 확인
//
//        // DTO를 엔티티로 변환
//        Store store = dtoToEntity(storeDTO);
//
//        // 저장
//        storeRepository.save(store);
//
//        return store.getP_address();
//    }
