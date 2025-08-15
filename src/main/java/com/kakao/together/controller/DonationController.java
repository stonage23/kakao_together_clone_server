package com.kakao.together.controller;

import com.kakao.together.controller.dto.DonationDto.DonationRequest;
import com.kakao.together.facade.DonationFacade;
import com.kakao.together.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationFacade donationFacade;

    @PostMapping("")
    public ResponseEntity<Void> createDonation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DonationRequest request) {
        donationFacade.createDonation(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{donationId}/cancel")
    public ResponseEntity<Void> cancelDonation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(value = "donationId") Long donationId) {
        donationFacade.cancelDonation(userDetails, donationId);
        return ResponseEntity.ok().build();
    }
}
