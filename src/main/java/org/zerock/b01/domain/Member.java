package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String mid; // 기본 키

    @Column(length = 40, nullable = false)
    private String m_pw; //비밀번호

    @Column(length = 100, nullable = false)
    private String m_name; // 사용자 이름

    @Column(length = 40, nullable = false)
    private String m_email; // 사용자 이메일

    @Column(length = 30)
    private String m_phone; // 전화번호

    private String m_birth; // 생년월일

    @Column(length = 100)
    private String m_address; // 주소(거주지)
    @Column(length = 4)
    private String m_mbti; // MBTI
    private String m_gender; // 성별(man, woman)

    private boolean m_del; // 자동 로그인 여부
    private boolean m_social; // 소셜 로그인 여부

    // 찜한 장소와의 관계 (ManyToOne 관계는 Store 엔티티에서 설정)
    // @OneToMany(mappedBy = "bookmark")
    // private Set<Store> bookmarks; // 사용자가 찜한 Store 리스트 (생략 가능)
}
