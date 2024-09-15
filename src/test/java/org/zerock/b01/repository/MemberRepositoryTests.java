package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedToday = today.format(formatter);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers(){

        IntStream.rangeClosed(1, 100).forEach(i -> {

            Member member = Member.builder()
                    .mid("member"+i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email"+i+"@aaa.bbb")
                    .name("name"+i)
                    .phone("000-0000-"+i)
                    .address("은평구 응암동"+i)
                    .birth(formattedToday)
                    .gender("man")
                    .mbti("ISTP")
                    .build();

            member.addRole(MemberRole.USER);

            if(i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        });
    }

    @Test
    public void testRead() {

        Optional<Member> result = memberRepository.getWithRoles("member1");

        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());

        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));

    }

    @Commit
    @Test
    public void testUpdatepassword(){
        String mid = "member1";
        String mpw = passwordEncoder.encode("4444");
        memberRepository.updatePassword(mpw,mid);
    }

    @Commit
    @Test
    public void testUpdate(){
        String mid = "member1";
        String mpw = passwordEncoder.encode("4444");
        String email = "qbsb147@naver.com";
        String name = "여기";
        String phone= "000-0000-0000";
        String address = "은평구 증산동";
        String birth = formattedToday;
        String gender = "woman";
        memberRepository.updateAccount(mpw,mid, email, name, phone, address, birth, gender);
    }
}