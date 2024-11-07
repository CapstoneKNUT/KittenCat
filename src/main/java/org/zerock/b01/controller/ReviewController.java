package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReviewDTO;
import org.zerock.b01.service.ReviewService;


@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 목록
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ReviewDTO>> listReviews(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReviewDTO> reviews = reviewService.list(pageRequestDTO);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 읽기
    @GetMapping("/read")
    public ResponseEntity<ReviewDTO> readReview(@RequestParam Long rno) {
        ReviewDTO review = reviewService.readReview(rno);
        return ResponseEntity.ok(review);
    }

    // 리뷰 등록
    @PostMapping("/register")
    public ResponseEntity<String> registerReview(@RequestBody ReviewDTO reviewDTO, @RequestParam String userId) {
        reviewDTO.setWriter(userId); // UserContext에서 가져온 사용자 ID 설정
        reviewService.registerReview(reviewDTO);
        return ResponseEntity.ok("Review registered successfully");
    }

    // 리뷰 수정
    @PutMapping("/edit")
    public ResponseEntity<String> modifyReview(@RequestBody ReviewDTO reviewDTO, @RequestParam String userId) {
        if (!reviewDTO.getWriter().equals(userId)) {
            return ResponseEntity.ok("You are not allowed to modify this review");
        }
        reviewService.modifyReview(reviewDTO);
        return ResponseEntity.ok("Review modified successfully");
    }

    // 리뷰 삭제
    @PostMapping("/remove")
    public ResponseEntity<String> removeReview(@RequestParam Long rno, @RequestParam String userId) {
        ReviewDTO review = reviewService.readReview(rno);
        if (!review.getWriter().equals(userId)) {
            return ResponseEntity.ok("You are not allowed to remove this review");
        }
        reviewService.removeReview(rno);
        return ResponseEntity.ok("Review removed successfully");
    }
}
