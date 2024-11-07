package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.Review;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReviewDTO;
import org.zerock.b01.repository.ReviewRepository;
import org.zerock.b01.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<ReviewDTO> list(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("rno");

        Page<Review> result = reviewRepository.findAll(pageable);

        List<ReviewDTO> dtoList = result.getContent().stream()
                .map(review -> modelMapper.map(review,ReviewDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<ReviewDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public ReviewDTO readReview(Long rno) {

        Optional<Review> result = reviewRepository.findById(rno);

        Review review = result.orElseThrow();

        return entityToDTO(review);
    }

    @Override
    public Long registerReview(ReviewDTO reviewDTO) {
        log.info("Registering new review: {}", reviewDTO);

        Member member = memberRepository.findById(reviewDTO.getWriter())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 빌더 패턴을 사용하여 Review 객체 생성
        Review review = Review.builder()
                .content(reviewDTO.getContent())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .member(member)
                .build();

        review = reviewRepository.save(review);
        log.info("Review registered successfully with ID: {}", review.getRno());

        return review.getRno();  // ID 반환
    }



    @Override
    public void modifyReview(ReviewDTO reviewDTO) {
        log.info("Modifying review with ID: {}", reviewDTO.getRno());

        // 리뷰 수정
        Review review = reviewRepository.findById(reviewDTO.getRno())
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewDTO.getRno());
                    return new RuntimeException("Review not found");
                });

        review.setContent(reviewDTO.getContent());
        review.setModifiedDate(LocalDateTime.now());

        reviewRepository.save(review); // 수정된 내용 저장
        log.info("Review modified successfully with ID: {}", review.getRno());
    }

    @Override
    public void removeReview(Long rno) {
        log.info("Removing review with ID: {}", rno);

        // 리뷰 삭제
        reviewRepository.deleteById(rno);
        log.info("Review removed successfully with ID: {}", rno);
    }
}