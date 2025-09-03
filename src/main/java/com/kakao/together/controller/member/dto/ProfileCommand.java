package com.kakao.together.controller.member.dto;

import com.kakao.together.domain.entity.member.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ProfileCommand {

    private String nickname;
    private String profileImage;

    public static ProfileCommand fromEntity(Profile profile, String profileImage) {
        return ProfileCommand.builder()
                .nickname(profile.getNickname())
                .profileImage(profileImage)
                .build();
    }
}
