package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review")


public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;  // 리뷰 ID

    @ManyToOne
    @JoinColumn(name = "mid", nullable = false) // Member의 기본 키와 연관
    private Member member;  // 작성자 (Member 엔티티와 연관)

    private String content;  // 리뷰 내용
/*    private int rating;      // 평점*/
    private LocalDateTime createdDate; // 생성일
    private LocalDateTime modifiedDate; // 수정일 (수정 시 사용)
}
