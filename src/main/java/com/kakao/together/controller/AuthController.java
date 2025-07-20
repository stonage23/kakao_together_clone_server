package com.kakao.together.controller;

import com.kakao.together.controller.dto.AuthDto.SignupByEmailRequest;
import com.kakao.together.facade.AuthFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
