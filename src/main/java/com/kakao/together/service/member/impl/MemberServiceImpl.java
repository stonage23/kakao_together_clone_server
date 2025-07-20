package com.kakao.together.service.member.impl;

import com.kakao.together.controller.dto.AuthDto;
import com.kakao.together.controller.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.domain.entity.member.Authority;
import com.kakao.together.domain.entity.member.Member;
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
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .address(request.getAddress())
                .authority(Authority.USER)
                .build();
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
    public void updatePassword(ResetPasswordRequest reqeustDto) {
        Member member = memberRepository.findByEmail(reqeustDto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        member.updatePassword(passwordEncoder.encode(reqeustDto.getPassword()));
    }

    @Override
    public void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        if (passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        memberRepository.delete(member);
    }


}
