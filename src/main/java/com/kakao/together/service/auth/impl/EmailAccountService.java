package com.kakao.together.service.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.LoginRequest;
import com.kakao.together.controller.auth.dto.AuthDto.LogoutRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.token.dto.TokenContainer;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.auth.AuthService;
import com.kakao.together.service.cache.CacheService;
import com.kakao.together.service.mail.MailService;
import com.kakao.together.service.member.MemberService;
import com.kakao.together.service.token.RefreshTokenRepository;
import com.kakao.together.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAccountService implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final MailService mailService;
    private final TokenService tokenService;
    private final RefreshTokenRepository<String> refreshTokenRepository;
    private final CacheService cacheService;

    private static final String EMAIL_PREFIX = "email ";

    @Override
    public void processEmailSignupRequest(SignupByEmailRequest request) {

        if (memberService.isExistsEmail(request.getEmail()))
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
            throw new CustomException(e, "회원가입 인증메일코드로 조회 중 예외 발생");
        }
        if (savedRequest == null) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }
        memberService.createMember(savedRequest);
        cacheService.deleteData(EMAIL_PREFIX + code);
    }

    @Override
    public TokenContainer login(LoginRequest request) {

        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
        }

        TokenContainer tokenContainer = tokenService.generateTokenContainer(
                authentication.getName()
                , Map.of("auth", authentication.getAuthorities()));
        refreshTokenRepository.saveRefreshToken(tokenContainer.getRefreshToken(), authentication.getName());

        return tokenContainer;
    }

    @Override
    public void logout(UserDetails principal, LogoutRequest request) {

        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            log.warn("refresh토큰이 없는 상태에서 로그아웃 요청; 요청한 유저id: {}", principal.getUsername());
            throw new CustomException(ErrorCode.FAILED_LOGOUT, "refresh토큰이 존재하지 않습니다.");
        }

        if (refreshTokenRepository.findRefreshToken(request.getRefreshToken()).isEmpty()) {
            log.info("이미 로그아웃됨; 요청한 유저id: {}; 토큰: {}", principal.getUsername(), request.getRefreshToken());
        } else {
            refreshTokenRepository.deleteRefreshToken(request.getRefreshToken());
            log.info("유저 로그아웃; memberId: {}", principal.getUsername());
        }
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        if (!memberService.isExistsEmail(email)) {
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
    public void deleteMember(String username, DeleteMemberRequest requestDto) {
        memberService.deleteMember(Long.valueOf(username), requestDto);
        log.info("계정 삭제 요청 처리 완료; memberId: {}", username);
    }
}