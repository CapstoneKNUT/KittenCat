package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

public interface MemberService {
    // 사용자 ID가 이미 존재하는 경우 발생하는 예외
    static class MidExistException extends Exception {
    }

    // 회원 등록 메소드
    String register(MemberDTO memberDTO) throws MidExistException;

    // 로그인 메소드
    MemberDTO login(MemberDTO memberDTO) throws IllegalArgumentException;

    // 로그아웃 메소드 (여기서는 세션 무효화 등의 로직을 처리)
    void logout(String mid); // 세션을 무효화하거나 추가적인 로그아웃 로직을 처리할 수 있습니다.

    // 회원 정보 조회 메소드 (ID로)
    MemberDTO read(String m_id);

    // 회원 정보 수정 메소드
    MemberDTO modify(String m_id, MemberDTO memberDTO); // 수정할 필드를 포함한 DTO를 받는 메소드
}
