package com.kakao.together.facade;

import com.kakao.together.controller.TokenContainer;
import com.kakao.together.controller.dto.AuthDto;
import com.kakao.together.controller.dto.EmailTemplateDto;
import com.kakao.together.email.EmailBuilder;
import com.kakao.together.email.EmailService;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.jwt.JwtService;
import com.kakao.together.redis.RedisService;
import com.kakao.together.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthFacade {

    private final MemberService memberService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RedisService redisService;

    private static final String EMAIL_SENDER = "카카오투게더";
    private static final String EMAIL_PREFIX = "email ";
    private static final String REFRESH_TOKEN_FREFIX = "refresh_token";

    public void saveTempTokenAndSendValidationMail(SignupByEmailRequest request) {
        if(memberService.isPresentEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        EmailBuilder emailInfo = EmailBuilder.builder()
                .sender(EMAIL_SENDER)
                .recipient(request.getEmail())
                .subject("인증하시고 서비스를 이용해주세요!")
                .text(EmailTemplateDto.SignUpVerification.getTemplate())
                .build();

        emailService.sendMail(emailInfo);

        String code = EmailTemplateDto.SignUpVerification.getCode();
        redisService.setSingleData(EMAIL_PREFIX + code, request);
    }

    public void validateSignup(String code) {
        SignupByEmailRequest savedRequest = (SignupByEmailRequest) redisService.getSingleData(EMAIL_PREFIX + code);
        memberService.createMember(savedRequest);
        redisService.deleteSingleData(EMAIL_PREFIX + code);
    }

    public TokenContainer login(AuthDto.LoginRequest requestDto) {
        if (!memberService.isEqualPassword(requestDto.getUsername(), requestDto.getPassword()))
            throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", requestDto.getUsername());

        TokenContainer tokenContainer = jwtService.generateTokenContainerWithCommonClaims(claims);
        redisService.setSingleData(REFRESH_TOKEN_FREFIX, tokenContainer.getRefreshToken());

        return tokenContainer;
    }

    // TODO 로깅 필요없으면 안남겨도됨
    public void logout(String username) {
        if (String.valueOf(redisService.getSingleData(REFRESH_TOKEN_FREFIX)+username).isEmpty()) {
            log.info("refresh토큰이 만료되었거나 없는 상태에서 로그아웃 요청");
        }
        redisService.deleteSingleData(EMAIL_PREFIX + username);
    }
}