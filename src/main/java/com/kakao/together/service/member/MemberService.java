package com.kakao.together.service.member;

import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStatusResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;
import com.kakao.together.domain.entity.member.Member;
import org.springframework.stereotype.Service;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
public interface MemberService {

    void checkEmailDuplication(String email);

    void updatePassword(ResetPasswordRequest reqeustDto);

    void deleteMember(Long memberId, DeleteMemberRequest requestDto);

    void checkNicknameDuplication(String nickname);

    MeDetailResponse getMyDetail(Long username);

    void updateProfile(String username, ProfileUpdateRequest profileReq);

    DonationStatusResponse getMyTotalDonationStatus(Long memberId);

    Member getMember(Long memberId);

    void handleSignupRequest(SignupByEmailRequest request);

    void activateMember(String code);
}
