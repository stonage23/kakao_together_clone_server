package com.kakao.together.service.auth.impl;

import com.kakao.together.controller.auth.dto.AuthDto.LoginRequest;
import com.kakao.together.controller.auth.dto.AuthDto.LogoutRequest;
import com.kakao.together.controller.token.dto.RefreshTokenDto.TokenRefreshRequest;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.auth.AuthService;
import com.kakao.together.service.cache.CacheService;
import com.kakao.together.service.mail.MailService;
import com.kakao.together.service.token.RefreshTokenRepository;
import com.kakao.together.token.TokenContainer;
import com.kakao.together.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CacheService cacheService;

    private static final String EMAIL_PREFIX = "email ";

    @Value("${business.constants.token.password-reset.expiring}")
    private Integer PASSWORD_RESET_EXPIRING;

    @Override
    public TokenContainer login(LoginRequest request) {

        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.info("아이디 또는 비밀번호가 틀림. id={}, password={}", request.getLoginId(), request.getPassword());
            throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
        }

        TokenContainer tokenContainer = tokenProvider.generateTokenContainer(
                authentication);
        refreshTokenRepository.saveRefreshToken(tokenContainer.getRefreshToken(), authentication.getName());

        return tokenContainer;
    }

    @Override
    public void logout(UserDetails principal, LogoutRequest request) {

        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            log.warn("refresh토큰이 없는 상태에서 로그아웃 요청; 요청한 유저id: {}", principal.getUsername());
            throw new CustomException(ErrorCode.FAILED_LOGOUT);
        }

        if (refreshTokenRepository.findRefreshToken(request.getRefreshToken()).isEmpty()) {
            log.info("이미 로그아웃됨; 요청한 유저id: {}; 토큰: {}", principal.getUsername(), request.getRefreshToken());
        } else {
            refreshTokenRepository.deleteRefreshToken(request.getRefreshToken());
            log.info("유저 로그아웃; memberId: {}", principal.getUsername());
        }
    }

    /**
     * 사용자 식별 코드를 URL에 포함시켜서 이메일 발송. 식별 코드는 일정 시간동안 서버에서 보관(key:value = code:email)
     * @param email
     */
    @Override
    public void sendPasswordResetEmail(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER, "존재하지 않는 이메일입니다.");
        }

        String code = mailService.sendPasswordResetMail(email);

        cacheService.setData(EMAIL_PREFIX + code, email, PASSWORD_RESET_EXPIRING);
    }

    @Override
    public TokenContainer refreshToken(TokenRefreshRequest request) {

        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("access, refresh 토큰 재발급 실패: refreshToken이 없는 요청");
            throw new CustomException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        String result = refreshTokenRepository.findRefreshToken(request.getRefreshToken());
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        String oldToken = request.getRefreshToken();
        Authentication authentication = tokenProvider.getAuthentication(oldToken);

        TokenContainer tokenContainer = tokenProvider.generateTokenContainer(authentication);
        refreshTokenRepository.deleteRefreshToken(oldToken);
        refreshTokenRepository.saveRefreshToken(tokenContainer.getRefreshToken(), authentication.getName());

        return tokenContainer;
    }
}