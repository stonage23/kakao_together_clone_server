package com.kakao.together.repository;

import com.kakao.together.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// NOTE Jwt 인증을 위한 임시 MemberRepository
// TODO MemberService 구현 이후 실제 사용할 MemberRepository로 교체
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);
}
