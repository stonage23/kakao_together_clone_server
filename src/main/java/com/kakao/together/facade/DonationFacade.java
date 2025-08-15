package com.kakao.together.facade;

import com.kakao.together.controller.dto.DonationDto.DonationRequest;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.payment.PaymentTransaction;
import com.kakao.together.paymentgate.service.PortOnePaymentValidationService;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationFacade {

    private final DonationService donationService;
    private final PortOnePaymentValidationService portOnePaymentValidationService;

    @Transactional
    public void createDonation(CustomUserDetails userDetails, DonationRequest request) {
        portOnePaymentValidationService.verifyPayment(request.getImpUid());
        donationService.createDonation(request, userDetails.getId());
    }

    @Transactional
    public void cancelDonation(CustomUserDetails userDetails, Long donationId) {
        Donation donation = donationService.getDonationEntity(donationId);
        PaymentTransaction paymentTransaction = donation.getPaymentTransaction();
        if (paymentTransaction == null) {
            log.error("존재해야하는 결제 내역(PaymentTransaction)이 존재하지 않음; donationId: {}", donationId);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "기부의 결제 내역 조회 불가; donationId: " + donationId);
        }
        portOnePaymentValidationService.refundPayment(paymentTransaction.getImpUid());
        donationService.cancelDonationById(donationId);
    }
}
