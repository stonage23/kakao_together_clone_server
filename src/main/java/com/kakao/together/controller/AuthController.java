package com.kakao.together.controller;

import com.kakao.together.controller.dto.AuthDto.LoginRequest;
import com.kakao.together.controller.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;
import com.kakao.together.facade.AuthFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/auth/signup")
    public ResponseEntity signupRequest(@RequestBody @Valid SignupByEmailRequest requestDto) {
        authFacade.saveTempTokenAndSendValidationMail(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/validation/{code}")
    public ResponseEntity signupValidate(@PathVariable("code") String code) {
        authFacade.validateSignup(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest requestDto) {
        TokenContainer tokenContainer = authFacade.login(requestDto);
        return ResponseEntity.ok()
                .headers(tokenContainer.getHttpHeaders())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails principal) {
        authFacade.logout(principal.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/password-reset/request/{email}")
    public ResponseEntity<Void> sendPasswordResetRequestMail(@PathVariable String email) {
        authFacade.sendPasswordResetEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/password-reset/{code}")
    public ResponseEntity<Object> checkPasswordResetCode(@PathVariable String code) {
        String email = authFacade.checkPasswordResetCode(code);
        Map<String, String> data = Map.of("email", email, "code", code);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/auth/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest reqeustDto) {
        reqeustDto.checkPasswordMatch();
        authFacade.resetPassword(reqeustDto);
        return ResponseEntity.ok().build();
    }
}
