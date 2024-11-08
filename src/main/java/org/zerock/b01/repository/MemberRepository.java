package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Member;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // ID로 회원을 찾는 메서드
    Optional<Member> findByMid(String mid);
}
