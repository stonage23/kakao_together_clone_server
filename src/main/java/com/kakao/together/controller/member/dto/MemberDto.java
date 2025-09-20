package com.kakao.together.controller.member.dto;

import com.kakao.together.domain.entity.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class EmailDuplicateCheckRequest {
        @Email
        private String email;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ProfileUpdateRequest {

        @NotBlank(message = "사용하실 닉네임을 입력해주세요.")
        @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 입력해주세요.")
        private String nickname;
        private Long imageId;
        private String birth;
        private String address;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class MeDetailResponse {

        private Long id;
        private String email;
        private String nickname;
        private String accountStatus;
        private String profileImageUrl;
        private String birth;
        private String address;

        public static MeDetailResponse fromEntity(Member member, String profileImageUrl) {
            return MeDetailResponse.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getProfile().getNickname())
                    .accountStatus(member.getMemberStatus().getValue())
                    .profileImageUrl(profileImageUrl)
                    .birth(member.getProfile().getBirth())
                    .address(member.getProfile().getAddress())
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationStateResponse {
        private Long directDonationAmount;
        private Long directDonationCount;
        private Long donationAmount;
        private Long donationCount;
        private Long indirectDonationAmount;
        private Long commentDonationCount;
    }
}
