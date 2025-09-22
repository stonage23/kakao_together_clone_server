package com.kakao.together.service.donation.impl;

import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.external.paymentgate.service.impl.PaymentGateRefundService;
import com.kakao.together.external.paymentgate.exception.PaymentGateException;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.donation.DonationCancelService;
import com.kakao.together.service.donation.DonationService;
import com.kakao.together.service.payment.transaction.PaymentTransactionService;
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
    private final PaymentGateRefundService paymentGateRefundService;
    private final PaymentTransactionService paymentTransactionService;

    @Override
    @Transactional
    public void cancelByMember(Long memberId, Long donationId) {

        try {
            Donation donation = donationService.getDonation(donationId, memberId);

            if (donation.getStatus() == DonationStatus.CANCELLED) {
                throw new CustomException(ErrorCode.ALREADY_CANCELLED_DONATION);
            }

            String impUid = donation.getPaymentTransaction().getImpUid();
            PaymentResponse response = paymentGateRefundService.refundPayment(impUid);
            paymentTransactionService.cancelPayment(donation.getPaymentTransaction().getId(), response.getCancelledAt());

            donationService.cancelByMember(donationId);

        } catch (NoSuchElementException e) {
            log.warn("존재하지 않는 기부내역으로 인해 기부 취소 실패. memberId= {}, donationId= {}", memberId, donationId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION, "기부 취소 처리도중 오류발생. 관리자에게 문의해주세요.");
        } catch (IllegalStateException e) {
            log.warn("적절하지 않은 내부상태로 기부 취소 실패. memberId= {}, donationId= {}", memberId, donationId);
            donationService.failCancelDonation(donationId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION, "기부 취소 처리도중 오류발생. 관리자에게 문의해주세요.");
        } catch (PaymentGateException e) {
            log.warn("유저가 요청한 환불처리 실패. donationId: {}", donationId, e);
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL, "결제 취소 처리도중 오류발생. 잠시후 다시 시도해주세요.");
        }

    }

    @Override
    @Transactional
    public void cancelByAdmin(Long donationId) {

        try {
            Donation donation = donationService.getDonation(donationId);
            donation.cancelByAdmin();

            String impUid = donation.getPaymentTransaction().getImpUid();
            PaymentResponse response = paymentGateRefundService.refundPayment(impUid);
            paymentTransactionService.cancelPayment(donation.getPaymentTransaction().getId(), response.getCancelledAt());
        } catch (NoSuchElementException | IllegalStateException e) {
            log.warn("관리자가 요청한 기부 취소 처리 실패. donationId= {}", donationId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION, "기부 취소 처리도중 오류발생. 서버 관리자에게 문의해주세요.");
        } catch (PaymentGateException e) {
            log.warn("관리자가 요청한 환불처리 실패. donationId: {}", donationId, e);
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL, "결제 취소 처리도중 오류발생. 잠시후 다시 시도해주세요.");
        }
    }
}
