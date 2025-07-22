package com.kakao.together.service.member.impl;

import com.kakao.together.controller.dto.AuthDto;
import com.kakao.together.controller.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.dto.MemberDto.MyProfileResponse;
import com.kakao.together.controller.dto.MemberDto.ProfileUpdateRequest;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.Profile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.repository.MemberRepository;
import com.kakao.together.service.member.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;
import static com.kakao.together.controller.dto.MemberDto.MemberData;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createMember(SignupByEmailRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent((member) -> {
                    log.error("이미 존재하는 이메일로 계정생성 시도: " + member.getEmail());
                    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
                });
        Member member = request.toEntity();
        memberRepository.save(member);
    }

    @Override
    public MemberData findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 이메일에 해당하는 유저 정보를 DB에서 조회하지 못함");
        });

        return MemberData.fromEntity(member);
    }

    @Override
    public boolean isPresentEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isEqualPassword(String username, String password) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        return passwordEncoder.matches(password, member.getPassword());
    }

    @Override
    @Transactional
    public void updatePassword(ResetPasswordRequest reqeustDto) {
        Member member = memberRepository.findByEmail(reqeustDto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        member.updatePassword(passwordEncoder.encode(reqeustDto.getPassword()));
    }

    @Override
    @Transactional
    public void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        if (passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        memberRepository.delete(member);
    }

    @Override
    public boolean isPresentNickname(String nickname) {
        return memberRepository.existsByProfile_Nickname(nickname);
    }

    @Override
    public MyProfileResponse getProfile(String username) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        try {
            Profile profile = member.getProfile();
            return MyProfileResponse.fromEntity(profile);
        } catch (NoSuchElementException e) {
            log.error("##### 존재해야하는 Profile이 존재하지 않음");
            throw new CustomException(ErrorCode.NOT_FOUND_PROFILE);
        }
    }

    @Override
    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest profileReq) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        member.updateProfile(profileReq.toEntity());
    }
}
