/*
package org.zerock.b01.plus;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.MemberDTO;
import org.zerock.b01.dto.StoreDTO;
import org.zerock.b01.service.MemberService;
import org.zerock.b01.service.StoreService;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@Log4j2
public class StoreServiceTests {

    @Autowired
    private StoreService storeService; // StoreService 주입

    @Autowired
    private MemberService memberService; // MemberService 주입

    @Test
    public void testRegister() {
        log.info(storeService.getClass().getName());

        // 테스트용 이미지 데이터 생성
        byte[] imageData = "test image data".getBytes(StandardCharsets.UTF_8);

        // MemberDTO 객체 생성
        MemberDTO memberDTO = MemberDTO.builder()
                .mid("user1")
                .m_pw("password")
                .m_email("example@example.com")
                .m_name("유저1")
                .m_phone("010-1234-5678")
                .m_address("충주시 대소원면")
                .m_birth("2024-10-08")
                .m_gender("male")
                .m_mbti("ISFP")
                .m_del(false)
                .m_social(false)
                .build();

        // Member를 저장하고 저장된 Member의 ID를 가져옵니다.
        String memberId = memberService.register(memberDTO);  // Member 등록
        MemberDTO savedMember = memberService.read(memberId); // 저장된 Member 읽기

        // StoreDTO 객체 생성
        StoreDTO storeDTO = StoreDTO.builder()
                .p_address("대한민국 어딘가")
                .bookmark(memberId) // 저장된 Member의 ID 사용
                .p_name("Beautiful Beach")
                .p_category("Beach")
//                .p_image(imageData)
                .p_star(4.5f)
                .build();

        // Store 등록
        String p_address = storeService.register(storeDTO);

        log.info("Stored Address: " + p_address);
    }

    // 문자열을 Date로 변환하는 메소드
    private Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            log.error("Date parsing error: " + e.getMessage());
            return null; // 예외 처리 필요
        }
    }
}
*/
