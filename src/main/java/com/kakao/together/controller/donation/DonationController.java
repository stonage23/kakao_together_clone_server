package com.kakao.together.controller.donation;

import com.kakao.together.controller.donation.dto.DonationDto.DonationCreateWithCommentWrapper;
import com.kakao.together.controller.donation.dto.DonationDto.DonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.facade.DonationFacade;
import com.kakao.together.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationFacade donationFacade;

    @PostMapping("/direct")
    public ResponseEntity<Void> createDonation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DonationRequest request) {
        donationFacade.createDonation(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{donationId}/cancel")
    public ResponseEntity<Void> cancelDonation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(value = "donationId") Long donationId) {
        donationFacade.cancelDonation(userDetails, donationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment-donation")
    public ResponseEntity<Void> createCommentDonation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DonationCreateWithCommentWrapper request) {
        donationFacade.createCommentDonation(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<DonationsResponse>> getAllMyDonations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DonationsResponse> response = donationFacade.getAllMyDonations(userDetails);
        return ResponseEntity.ok(response);
    }
}
