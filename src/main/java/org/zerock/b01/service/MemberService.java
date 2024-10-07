package org.zerock.b01.service;

import org.zerock.b01.dto.MemberDTO;

public interface MemberService {

    // 회원 등록 메소드
    String register(MemberDTO memberDTO);

    // 회원 정보 조회 메소드 (ID로)
    MemberDTO read(String m_id);
}
