package com.kakao.together.controller.auth;

import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.LoginRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;
import com.kakao.together.controller.dto.TokenContainer;
import com.kakao.together.service.auth.impl.EmailAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces="application/json;charset=UTF-8")
public class AuthController {

    private final EmailAccountService emailAccountService;

    @PostMapping("/auth/signup")
    public ResponseEntity registerMemberAndSendEmail(@RequestBody @Valid SignupByEmailRequest requestDto) {
        emailAccountService.processEmailSignupRequest(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/validation/{code}")
    public ResponseEntity signupValidate(@PathVariable("code") String code) {
        emailAccountService.validateSignup(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest requestDto) {
        TokenContainer tokenContainer = emailAccountService.login(requestDto);
        return ResponseEntity.ok()
                .headers(tokenContainer.getHttpHeaders())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails principal) {
        emailAccountService.logout(principal.getUsername());
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
