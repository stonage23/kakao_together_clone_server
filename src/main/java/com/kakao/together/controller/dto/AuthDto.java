package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.member.Role;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.Profile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
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
                    .role(Role.MEMBER)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class LoginRequest {

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

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ResetPasswordRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String code;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;
        @NotBlank(message = "확인 비밀번호를 입력해주세요.")
        private String checkPassword;

        public void checkPasswordMatch() {
            if (!this.password.equals(this.checkPassword)) throw new CustomException(ErrorCode.NOT_MATCH_CHECKPASSWORD);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DeleteMemberRequest {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;
    }
}
