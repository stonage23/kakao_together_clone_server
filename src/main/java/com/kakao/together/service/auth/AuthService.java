package com.kakao.together.service.auth;

import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.auth.dto.AuthDto.LogoutRequest;
import com.kakao.together.controller.token.dto.TokenContainer;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    void processEmailSignupRequest(AuthDto.SignupByEmailRequest request);

    void validateSignup(String code);

    TokenContainer login(AuthDto.LoginRequest requestDto);

    void logout(UserDetails principal, LogoutRequest request);

    void sendPasswordResetEmail(String email);

    boolean checkPasswordResetCode(String code, String email);

    void resetPassword(AuthDto.ResetPasswordRequest reqeustDto);

    void deleteMember(String username, AuthDto.DeleteMemberRequest requestDto);
}
