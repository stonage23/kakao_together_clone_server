package com.kakao.together.controller.auth;

import com.kakao.together.controller.auth.dto.AuthDto.*;
import com.kakao.together.controller.token.TokenContainer;
import com.kakao.together.service.auth.impl.EmailAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces="application/json;charset=UTF-8")
@Slf4j
public class AuthController {

    private final EmailAccountService emailAccountService;

    @PostMapping("/auth/signup")
    public ResponseEntity registerMemberAndSendEmail(@RequestBody @Valid SignupByEmailRequest requestDto) {
        emailAccountService.processEmailSignupRequest(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/validation")
    public ResponseEntity signupValidate(@RequestParam String code) {
        emailAccountService.validateSignup(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenContainer> login(@RequestBody @Valid LoginRequest requestDto) {
        TokenContainer token = emailAccountService.login(requestDto);
        log.info(requestDto.getLoginId());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails principal, @RequestBody LogoutRequest request) {
        emailAccountService.logout(principal, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/password-reset/request/{email}")
    public ResponseEntity<Void> sendPasswordResetRequestMail(@PathVariable String email) {
        emailAccountService.sendPasswordResetEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/password-reset/{code}")
    public ResponseEntity<Boolean> checkPasswordResetCode(@PathVariable String code, @RequestParam String email) {
        boolean isValidated = emailAccountService.checkPasswordResetCode(code, email);
        return ResponseEntity.ok().body(isValidated);
    }

    @PostMapping("/auth/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest reqeustDto) {
        reqeustDto.checkPasswordMatch();
        emailAccountService.resetPassword(reqeustDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/auth/delete")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserDetails principal, @RequestBody DeleteMemberRequest requestDto) {
        emailAccountService.deleteMember(principal.getUsername(), requestDto);
        return ResponseEntity.ok().build();
    }
}
