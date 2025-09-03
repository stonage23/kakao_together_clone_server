package com.kakao.together.service.auth;

import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.dto.TokenContainer;

public interface AuthService {
    void processEmailSignupRequest(AuthDto.SignupByEmailRequest request);

    void validateSignup(String code);

    TokenContainer login(AuthDto.LoginRequest requestDto);

    void logout(String username);

    void sendPasswordResetEmail(String email);

    boolean checkPasswordResetCode(String code, String email);

    void resetPassword(AuthDto.ResetPasswordRequest reqeustDto);

    void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto);
}
