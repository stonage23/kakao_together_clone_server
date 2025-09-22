package com.kakao.together.controller.donation;

import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.donation.DonationCancelService;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DonationCancelService donationCancelService;

    @PostMapping("/{donationId}/cancel")
    public ResponseEntity<Void> cancelByMember(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long donationId) {
        donationCancelService.cancelByMember(userDetails.getUserId(), donationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<DonationsResponse>> getAllMyDonations(@AuthenticationPrincipal CustomUserDetails userDetail) {
        List<DonationsResponse> response = donationService.getAllDonationsForDonor(userDetail.getUserId());
        return ResponseEntity.ok(response);
    }
}
