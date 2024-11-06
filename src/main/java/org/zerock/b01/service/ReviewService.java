package org.zerock.b01.service;

import org.zerock.b01.domain.Review;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReviewDTO;

public interface ReviewService {

    // 리뷰 리스트 (페이징 처리)
    PageResponseDTO<ReviewDTO> list(PageRequestDTO pageRequestDTO);

    // 리뷰 읽기
    ReviewDTO readReview(Long rno);

    // 리뷰 등록
    Long registerReview(ReviewDTO reviewDTO);

    // 리뷰 수정
    void modifyReview(ReviewDTO reviewDTO);

    // 리뷰 삭제
    void removeReview(Long rno);

    default ReviewDTO entityToDTO(Review review) {

        return ReviewDTO.builder()
                .rno(review.getRno())
                .content(review.getContent())
                .writer(review.getMember().getMid())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build();
    }
}
