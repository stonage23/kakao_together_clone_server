package com.kakao.together.service.member;

import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStateResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;
import org.springframework.stereotype.Service;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
public interface MemberService {

    void createMember(SignupByEmailRequest request);

    boolean checkEmailDuplicate(String email);

    boolean checkCredentials(String username, String password);

    void updatePassword(ResetPasswordRequest reqeustDto);

    void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto);

    boolean checkNicknameDuplicate(String nickname);

    MeDetailResponse getMyDetail(String username);

    void updateProfile(String username, ProfileUpdateRequest profileReq);

    DonationStateResponse getDonationState(Long memberId);
}
