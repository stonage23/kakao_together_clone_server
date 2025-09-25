package com.kakao.together.controller.member;

import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStatusResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.member.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<Void> registerMember(@RequestBody @Valid SignupByEmailRequest request) {
        memberService.handleSignupRequest(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> signupValidate(@PathVariable String code) {
        memberService.activateMember(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<Boolean> checkDuplication(@RequestParam(required = false) @Email String email, @RequestParam(required = false) String nickname) {
        if (email != null) {
            memberService.checkEmailDuplication(email);
        } else if (nickname != null) {
            memberService.checkNicknameDuplication(nickname);
        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST, "중복 확인할 데이터를 지정해주세요.");
        }

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        request.checkPasswordMatch();
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/detail")
    public ResponseEntity<MeDetailResponse> getMyDetails(@AuthenticationPrincipal CustomUserDetails principal) {
        MeDetailResponse response = memberService.getMyDetail(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<HttpStatus> updateProfile(@RequestBody ProfileUpdateRequest request,
                                                    @AuthenticationPrincipal UserDetails principle) {
        memberService.updateProfile(principle.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<DonationStatusResponse> getDonationState(@PathVariable Long id) {
        DonationStatusResponse state = memberService.getMyTotalDonationStatus(id);
        return ResponseEntity.ok(state);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody DeleteMemberRequest request) {
        memberService.deleteMember(principal.getUserId(), request);
        log.info("계정 삭제 요청 처리 완료; memberId: {}", principal.getUserId());
        return ResponseEntity.ok().build();
    }
}
