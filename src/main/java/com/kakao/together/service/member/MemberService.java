package com.kakao.together.service.member;

import com.kakao.together.controller.dto.AuthDto.ResetPasswordRequest;
import org.springframework.stereotype.Service;

import static com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;
import static com.kakao.together.controller.dto.MemberDto.MemberData;

@Service
public interface MemberService {

    void createMember(SignupByEmailRequest request);

    MemberData findMemberByEmail(String email);

    boolean isPresentEmail(String email);

    boolean isEqualPassword(String username, String password);

    void updatePassword(ResetPasswordRequest reqeustDto);
}
