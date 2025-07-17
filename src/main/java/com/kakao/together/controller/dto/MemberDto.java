package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// NOTE Jwt 인증을 위한 임시 MemberDto
// TODO MemberService 구현 이후 실제 사용할 MemberDto로 교체
public class MemberDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class MemberInfo {
        private String email;

        public static MemberInfo fromEntity(Member member) {
            return MemberInfo.builder()
                    .email(member.getEmail())
                    .build();
        }
    }
}
