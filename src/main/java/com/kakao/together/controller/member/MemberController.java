package com.kakao.together.controller.member;

import com.kakao.together.controller.member.dto.MemberDto.DonationStateResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.kakao.together.controller.member.dto.MemberDto.EmailDuplicateCheckRequest;
import static com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-duplicate-email")
    public ResponseEntity<Boolean> checkDuplicateEmail(@Valid @RequestBody EmailDuplicateCheckRequest request) {
        return ResponseEntity.ok(memberService.isExistsEmail(request.getEmail()));
    }

    @GetMapping("/check-duplicate-nickname/{nickname}")
    public ResponseEntity<Boolean> checkDuplicateNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @GetMapping("/me/detail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeDetailResponse> getMyDetails(@AuthenticationPrincipal UserDetails principal) {
        MeDetailResponse response = memberService.getMyDetail(principal.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<HttpStatus> updateProfile(@RequestBody ProfileUpdateRequest profileReq,
                                                    @AuthenticationPrincipal UserDetails principle) {
        memberService.updateProfile(principle.getUsername(), profileReq);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<DonationStateResponse> getDonationState(@PathVariable Long id) {
        DonationStateResponse state = memberService.getDonationState(id);
        return ResponseEntity.ok(state);
    }
}
