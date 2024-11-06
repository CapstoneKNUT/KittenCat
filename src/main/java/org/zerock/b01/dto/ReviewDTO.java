package org.zerock.b01.dto;

import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long rno;      // 리뷰 ID (Primary Key)
    private String title;       // 리뷰 제목
    private String content;     // 리뷰 내용
    private String writer;      // 작성자 ID (Member의 mid 사용)
    private LocalDateTime createdDate; // 등록일
    private LocalDateTime modifiedDate; // 수정일

    // 첨부파일 이름 리스트
    private List<String> fileNames;
}
