package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    @NotEmpty
    private String m_id; // 기본 키 (회원 ID)

    @NotEmpty
    private String m_pw; // 사용자 비밀번호

    @NotEmpty
    private String m_name; // 사용자 이름

    @NotEmpty
    private String m_email; // 사용자 이메일
    @NotEmpty
    private String m_phone; // 전화번호
    @NotEmpty
    private Date m_birth; // 생년월일
    @NotEmpty
    private String m_address; // 주소
    @NotEmpty
    private String m_mbti; // MBTI
    @NotEmpty
    private String m_gender; // 성별

    private boolean m_del; // 자동 로그인 여부
    private boolean m_social; // 소셜 로그인 여부
}
