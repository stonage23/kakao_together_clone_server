package com.kakao.together.service.donation.impl;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.exception.paymentgate.PaymentGateException;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.donation.DonationCancelService;
import com.kakao.together.service.donation.DonationService;
import com.kakao.together.service.payment.transaction.PaymentTransactionService;
import com.kakao.together.service.paymentgate.PaymentGateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationCancelServiceImpl implements DonationCancelService {

    private final DonationService donationService;
    private final PaymentGateClient paymentGateClient;
    private final PaymentTransactionService paymentTransactionService;

    @Override
    @Transactional
    // 처음에 취소 요청(transaction new), 취소 로직 완료 후 취소 완료 업데이트
    public void cancelByMember(Long memberId, Long donationId) {

        try {
            Donation donation = donationService.getDonation(donationId, memberId);
            donationService.cancelByMember(donationId);

            String impUid = donation.getPaymentTransaction().getImpUid();
            PaymentResponse response = paymentGateClient.refundPayment(impUid);
            paymentTransactionService.cancelPayment(donation.getPaymentTransaction().getId(), response.getCancelledAt());
        } catch (NoSuchElementException | IllegalStateException | PaymentGateException e) {
            log.error("유저가 요청한 환불처리 실패. donationId: {}", donationId, e);
            // TODO 관리자에게 직접적인 알림이 가는 로직이 필요할것 같음
            donationService.failCancelDonation(donationId);
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL, "결제 취소 처리도중 오류발생. 서버 관리자에게 문의 바랍니다.");
        }

    }

    @Override
    @Transactional
    public void cancelByAdmin(Long donationId) {

        try {
            Donation donation = donationService.getDonation(donationId);
            donation.cancelByAdmin();

            String impUid = donation.getPaymentTransaction().getImpUid();
            PaymentResponse response = paymentGateClient.refundPayment(impUid);
            paymentTransactionService.cancelPayment(donation.getPaymentTransaction().getId(), response.getCancelledAt());
        } catch (NoSuchElementException | IllegalStateException | PaymentGateException e) {
            log.error("관리자가 요청한 환불처리 실패. donationId: {}", donationId, e);
            donationService.failCancelDonation(donationId);
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL, "결제 취소 처리도중 오류발생. 서버 관리자에게 문의 바랍니다.");
        }

    }
}
