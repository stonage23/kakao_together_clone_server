package com.kakao.together.controller.admin;

import com.kakao.together.controller.donation.dto.DonationDto.DonationCompleteRequest;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.donation.DonationCancelService;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/donations")
@Slf4j
public class DonationAdminController {

    private final DonationService donationService;
    private final DonationCancelService donationCancelService;

    // TODO 기부 요청 하는 사람이랑 로그인 유저랑 일치하는지 로직 필요?
    // NOTE 기부 완료는 pg사 웹훅 요청에 의해 자동처리. 얘는 관리자 전용 핸들러로 옮겨야 할까?
    @PostMapping("/{donationId}/complete")
    public ResponseEntity<Void> completeDonation(@PathVariable Long donationId, @RequestBody DonationCompleteRequest request) {
        if (request.isValid(donationId)) {
            throw new CustomException(ErrorCode.INVALID_ARGUMENT, "donationId가 일치하지 않습니다.");
        }
        donationService.forceCompleteDonation(donationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long donationId) {
        donationCancelService.cancelByAdmin(donationId);
        return ResponseEntity.ok().build();
    }
}
