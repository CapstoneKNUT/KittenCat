package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "store")
public class Store {

    @Id
    @NotEmpty
    private String p_address; // 여행지 주소 (기본키)

    // 한 명의 사용자가 장소를 찜한 경우만 저장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id") // 외래 키로 mid 사용
    private Member bookmark; // 한 명의 사용자만 저장

    @Column(length = 200, nullable = false)
    private String p_name;

    @Column(length = 20, nullable = false)
    private String p_category;

    @Lob
    private byte[] p_image;

    private float p_star;
}
