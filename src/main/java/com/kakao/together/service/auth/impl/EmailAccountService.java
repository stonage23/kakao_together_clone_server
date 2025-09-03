package com.kakao.together.service.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kakao.together.service.cache.CacheService;
import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.dto.TokenContainer;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.auth.AuthService;
import com.kakao.together.service.mail.MailService;
import com.kakao.together.service.member.MemberService;
import com.kakao.together.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAccountService implements AuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final TokenService tokenService;
    private final CacheService cacheService;

    private static final String EMAIL_PREFIX = "email ";
    private static final String REFRESH_TOKEN_FREFIX = "refresh_token";

    @Override
    public void processEmailSignupRequest(SignupByEmailRequest request) {

        if (!memberService.checkEmailDuplicate(request.getEmail()))
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        String code = mailService.sendSignupMail(request.getEmail());

        try {
            cacheService.setData(EMAIL_PREFIX + code, request, 60 * 30);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 인증메일코드 서버 내부 저장 도중 예외 발생");
        }
    }

    @Override
    public void validateSignup(String code) {
        SignupByEmailRequest savedRequest;
        try {
            savedRequest = cacheService.getData(EMAIL_PREFIX + code, SignupByEmailRequest.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 인증메일코드로 조회 중 예외 발생");
        }
        if (savedRequest == null) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }
        memberService.createMember(savedRequest);
        cacheService.deleteData(EMAIL_PREFIX + code);
    }

    @Override
    public TokenContainer login(AuthDto.LoginRequest requestDto) {
        if (!memberService.checkCredentials(requestDto.getUsername(), requestDto.getPassword()))
            throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", requestDto.getUsername());

        TokenContainer tokenContainer = tokenService.generateTokenContainerWithCommonClaims(claims);
        cacheService.setData(REFRESH_TOKEN_FREFIX + requestDto.getUsername(), tokenContainer.getRefreshToken(), 60*30);

        return tokenContainer;
    }

    @Override
    public void logout(String username) {
        if ((cacheService.getData(REFRESH_TOKEN_FREFIX) + username).isEmpty()) {
            log.info("refresh토큰이 만료되었거나 없는 상태에서 로그아웃 요청");
            return;
        }
        cacheService.deleteData(REFRESH_TOKEN_FREFIX + username);
    }


    @Override
    public void sendPasswordResetEmail(String email) {
        if (!memberService.checkEmailDuplicate(email)) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER, "존재하지 않는 이메일입니다.");
        }

        String code = mailService.sendPasswordResetMail(email);

        cacheService.setData(EMAIL_PREFIX + code, email, 30*60);
    }

    @Override
    public boolean checkPasswordResetCode(String code, String email) {
        String savedEmail = cacheService.getData(EMAIL_PREFIX + code);
        if (savedEmail == null) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }
        if (!savedEmail.equals(email)) {
            return false;
        }
        return true;
    }

    @Override
    public void resetPassword(ResetPasswordRequest reqeustDto) {
        String email = cacheService.getData(EMAIL_PREFIX + reqeustDto.getCode());
        if (!email.equals(reqeustDto.getEmail())) {
            log.warn("적절하지 않은 이메일로 비밀번호 변경 시도");
            throw new CustomException(ErrorCode.NOT_AUTHENTICATE_USER);
        }
        memberService.updatePassword(reqeustDto);
    }

    @Override
    public void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto) {
        try {
            memberService.deleteMember(username, requestDto);
        } catch (Exception e) {
            log.error("회원정보 삭제 도중 예상치 못한 예외 발생");
            throw new CustomException(ErrorCode.FAILED_DELETE_MEMBER, e.getCause().getMessage());
        }
    }
}