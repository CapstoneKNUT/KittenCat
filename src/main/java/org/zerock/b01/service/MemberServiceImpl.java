package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.dto.MemberDTO;
import org.zerock.b01.repository.MemberRepository;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public String register(MemberDTO memberDTO) {
        // DTO를 엔티티로 변환
        Member member = dtoToEntity(memberDTO);

        // 회원 정보 저장
        memberRepository.save(member);

        // 저장된 회원의 ID 반환
        return member.getMid();
    }

    @Override
    public MemberDTO read(String m_id) {
        // 회원 정보 조회 (Optional로 반환)
        Optional<Member> result = memberRepository.findById(m_id);

        if (result.isPresent()) {
            // 엔티티를 DTO로 변환하여 반환
            return entityToDTO(result.get());
        } else {
            throw new IllegalArgumentException("Member not found: " + m_id);
        }
    }

    // DTO를 엔티티로 변환하는 메서드
    private Member dtoToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .mid(memberDTO.getMid())
                .m_pw(memberDTO.getM_pw())
                .m_email(memberDTO.getM_email())
                .m_name(memberDTO.getM_name())
                .m_phone(memberDTO.getM_phone())
                .m_address(memberDTO.getM_address())
                .m_birth(memberDTO.getM_birth())
                .m_gender(memberDTO.getM_gender())
                .m_mbti(memberDTO.getM_mbti())
                .m_del(memberDTO.isM_del())
                .m_social(memberDTO.isM_social())
                .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    private MemberDTO entityToDTO(Member member) {
        return MemberDTO.builder()
                .mid(member.getMid())
                .m_pw(member.getM_pw())
                .m_email(member.getM_email())
                .m_name(member.getM_name())
                .m_phone(member.getM_phone())
                .m_address(member.getM_address())
                .m_birth(member.getM_birth())
                .m_gender(member.getM_gender())
                .m_mbti(member.getM_mbti())
                .m_del(member.isM_del())
                .m_social(member.isM_social())
                .build();
    }
}
