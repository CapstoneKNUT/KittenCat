package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

//    @EntityGraph(attributePaths = "roleSet")
//    @Query("select m from Member m where m.mid = :mid and m.m_social = false")
//    Optional<Member> getWithRoles(@Param("mid") String mid);
//
//    @EntityGraph(attributePaths = "roleSet")
//    Optional<Member> findByEmail(String m_email);
//
//
//    @Modifying
//    @Transactional
//    @Query("update Member m set m.m_pw =:mpw where m.mid = :mid ")
//    void updatePassword(@Param("m_pw") String m_pw, @Param("mid") String mid);
//
//    @Modifying
//    @Transactional
//    @Query("update Member m set m.m_pw =:m_pw, m.m_email =:m_email, m.m_name =:m_name, m.m_phone =:m_phone, m.m_address =:m_address, m.m_birth =:m_birth, m.m_gender =:m_gender where m.mid = :mid ")
//    void updateAccount(@Param("m_pw") String m_pw, @Param("mid") String mid, @Param("m_email") String m_email, @Param("m_name") String m_name, @Param("m_phone") String m_phone, @Param("m_address") String m_address, @Param("m_birth") String m_birth, @Param("m_gender") String m_gender);

}
