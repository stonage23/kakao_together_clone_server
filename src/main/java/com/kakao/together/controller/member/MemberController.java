package com.kakao.together.controller.member;

import com.kakao.together.controller.member.dto.MemberDto.MyProfileResponse;
import com.kakao.together.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-duplicate/{email}")
    public ResponseEntity<Boolean> checkDuplicateEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.isPresentEmail(email));
    }

    @GetMapping("/check-duplicate/{nickname}")
    public ResponseEntity<Boolean> checkDuplicateNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.isPresentNickname(nickname));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<MyProfileResponse> getProfile(@AuthenticationPrincipal UserDetails principle) {
        MyProfileResponse response = memberService.getProfile(principle.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<HttpStatus> updateProfile(@RequestBody ProfileUpdateRequest profileReq,
                                                    @AuthenticationPrincipal UserDetails principle) {
        memberService.updateProfile(principle.getUsername(), profileReq);
        return ResponseEntity.ok().build();
    }
}
