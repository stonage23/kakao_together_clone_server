package com.kakao.together.service.member;

import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStateResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;
import com.kakao.together.domain.entity.member.Member;
import org.springframework.stereotype.Service;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
public interface MemberService {

    void createMember(SignupByEmailRequest request);

    boolean isExistsEmail(String email);

    boolean checkCredentials(String username, String password);

    void updatePassword(ResetPasswordRequest reqeustDto);

    void deleteMember(Long memberId, AuthDto.DeleteMemberRequest requestDto);

    boolean checkNicknameDuplicate(String nickname);

    MeDetailResponse getMyDetail(String username);

    void updateProfile(String username, ProfileUpdateRequest profileReq);

    DonationStateResponse getDonationState(Long memberId);

    Member getMember(Long memberId);
}
