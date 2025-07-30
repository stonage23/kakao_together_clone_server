package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.Profile;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    public static class ProfileUpdateRequest {

        @NotBlank(message = "사용하실 닉네임을 입력해주세요.")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하로 입력해주세요.")
        private String nickname;

        public Profile toEntity() {
            return Profile.builder()
                    .nickname(this.nickname)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class MyProfileResponse {

        private String nickname;

        public static MyProfileResponse fromEntity(Profile member) {
            return MyProfileResponse.builder()
                    .nickname(member.getNickname())
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class MemberData {
        private Long id;
        private String email;
        private String password;
        private String nickname;
        private String age;
        private String address;
        private Boolean isEmailVerified;

        public static MemberData fromEntity(Member member) {
            return MemberData.builder()
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

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Writer {
        private Long id;
        private String writerName;
        private String profileUrl;

        public static Writer fromEntity(Member member, Profile profile) {
            return Writer.builder()
                    .id(member.getId())
                    .writerName(profile.getNickname())
                    .profileUrl(profile.getProfileImage().getUrl())
                    .build();
        }
    }
}
