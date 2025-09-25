package com.kakao.together.service.auth;

import com.kakao.together.controller.auth.dto.AuthDto;
import com.kakao.together.controller.auth.dto.AuthDto.LogoutRequest;
import com.kakao.together.controller.token.dto.RefreshTokenDto;
import com.kakao.together.token.TokenContainer;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    TokenContainer login(AuthDto.LoginRequest requestDto);
    void logout(UserDetails principal, LogoutRequest request);
    void sendPasswordResetEmail(String email);

    TokenContainer refreshToken(RefreshTokenDto.TokenRefreshRequest request);
}
