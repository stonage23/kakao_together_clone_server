package com.kakao.together.controller.auth;

import com.kakao.together.controller.auth.dto.AuthDto.LoginRequest;
import com.kakao.together.controller.auth.dto.AuthDto.LogoutRequest;
import com.kakao.together.controller.member.dto.MemberDto.SendPasswordResetMailRequest;
import com.kakao.together.controller.token.dto.RefreshTokenDto.TokenRefreshRequest;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.auth.impl.AuthServiceImpl;
import com.kakao.together.token.TokenContainer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces="application/json;charset=UTF-8", path = "/api/auth")
@Slf4j
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<TokenContainer> login(@RequestBody @Valid LoginRequest requestDto) {
        TokenContainer token = authServiceImpl.login(requestDto);
        log.info(requestDto.getLoginId());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody LogoutRequest request) {
        authServiceImpl.logout(principal, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> sendPasswordResetMail(@RequestBody SendPasswordResetMailRequest request) {
        authServiceImpl.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenContainer> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenContainer tokenContainer = authServiceImpl.refreshToken(request);
        return ResponseEntity.ok(tokenContainer);
    }
}
