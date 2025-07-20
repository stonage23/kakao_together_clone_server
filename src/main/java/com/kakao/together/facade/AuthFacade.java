package com.kakao.together.facade;

import com.kakao.together.controller.dto.EmailTemplateDto;
import com.kakao.together.email.EmailBuilder;
import com.kakao.together.email.EmailService;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.redis.RedisService;
import com.kakao.together.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final MemberService memberService;
    private final EmailService emailService;
    private final RedisService redisService;

    private static String EMAIL_SENDER = "카카오투게더";
    private static String EMAIL_PREFIX = "email ";

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
}