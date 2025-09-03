package com.kakao.together.controller.member.dto;

import com.kakao.together.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class MemberCommand {
    private Long id;
    private String email;
    private String password;
    private String birth;
    private String address;
    private String role;
    private String memberStatus;
    private LocalDateTime deletedAt;

    public static MemberCommand fromEntity(Member member) {
        return MemberCommand.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .birth(member.getProfile().getBirth())
                .address(member.getProfile().getAddress())
                .role(member.getRole().getRole())
                .memberStatus(member.getMemberStatus().getValue())
                .deletedAt(member.getDeletedAt())
                .build();
    }
}