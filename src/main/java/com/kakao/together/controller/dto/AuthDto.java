package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.member.Authority;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AuthDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SignupByEmailRequest {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;

        @NotBlank(message = "사용하실 닉네임을 입력해주세요.")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하로 입력해주세요.")
        private String nickname;

        private String age;
        private String address;

        public Member toEntity() {
            Profile profile = Profile.builder()
                    .nickname(this.nickname)
                    .build();
            return Member.builder()
                    .email(this.email)
                    .password(this.password)
                    .age(this.age)
                    .address(this.address)
                    .profile(profile)
                    .authority(Authority.USER)
                    .build();
        }
    }
}
