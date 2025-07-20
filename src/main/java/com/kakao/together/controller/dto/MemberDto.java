package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class DeleteRequest {

        @NotBlank(message = "회원가입 당시 입력한 이메일로 로그인해주세요")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하로 입력해주세요.")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;

        public Member toEntity() {
            return Member.builder()
                    .email(this.username)
                    .password(this.password)
                    .build();
        }
    }
}
