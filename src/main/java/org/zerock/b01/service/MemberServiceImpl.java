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

    // 회원가입
    @Override
    public String register(MemberDTO memberDTO) throws MidExistException {
        String mid = memberDTO.getMid(); // mid를 memberDTO에서 가져옴
        boolean exist = memberRepository.existsById(mid);
        if (exist) {
            throw new MidExistException(); // 중복된 ID일 경우 예외 발생
        }
        // DTO를 엔티티로 변환
        Member member = dtoToEntity(memberDTO);
        // 회원 정보 저장
        memberRepository.save(member);
        // 저장된 회원의 ID 반환
        return member.getMid();
    }

    // 로그인
    @Override
    public MemberDTO login(MemberDTO memberDTO) throws IllegalArgumentException {
        // 로그인 로직
        Optional<Member> foundMemberOpt = memberRepository.findById(memberDTO.getMid());

        // 비밀번호 일치 여부 확인
        if (foundMemberOpt.isPresent()) {
            Member foundMember = foundMemberOpt.get();
            if (foundMember.getM_pw().equals(memberDTO.getM_pw())) {
                return entityToDTO(foundMember); // 로그인 성공 시 MemberDTO 반환
            }
        }
        throw new IllegalArgumentException("Invalid ID or password"); // 로그인 실패 시 예외 발생
    }

    // 회원정보 조회
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

    // 로그아웃
    @Override
    public void logout(String mid) {
        // 로그아웃 처리 로직 (예: 세션 무효화 등)
        log.info("User logged out: " + mid);
        // 세션 무효화 로직은 Controller에서 처리할 수 있으므로, 여기서는 로그만 남김
    }

    // 회원 정보 수정
    @Override
    public MemberDTO modify(String m_id, MemberDTO memberDTO) {
        Optional<Member> result = memberRepository.findById(m_id);
        if (result.isPresent()) {
            Member member = result.get();
            // 수정할 필드 업데이트
            member.setM_pw(memberDTO.getM_pw()); // 비밀번호 등 필요한 필드 수정
            member.setM_email(memberDTO.getM_email());
            member.setM_name(memberDTO.getM_name());
            member.setM_phone(memberDTO.getM_phone());
            member.setM_address(memberDTO.getM_address());
            member.setM_birth(memberDTO.getM_birth());
            member.setM_gender(memberDTO.getM_gender());
            member.setM_mbti(memberDTO.getM_mbti());
            member.setM_del(memberDTO.isM_del());
            member.setM_social(memberDTO.isM_social());

            // 업데이트된 회원 정보를 저장
            memberRepository.save(member);
            return entityToDTO(member); // 수정된 MemberDTO 반환
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
