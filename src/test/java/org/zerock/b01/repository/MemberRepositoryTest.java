package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.Member;


@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testInsert(){
        Member member = Member.builder()
                .mid("예찬")
                .m_pw("1111")
                .m_email("qbsb147@naver.com")
                .m_name("최예찬")
                .m_phone("010-6640-8235")
                .m_address("서울")
                .m_birth("1996")
                .m_gender("man")
                .m_mbti("ESTP")
                .m_del(false)
                .m_social(false)
                .build();
        Member result = memberRepository.save(member);
        log.info("MID: " + result.getMid());
    }


}