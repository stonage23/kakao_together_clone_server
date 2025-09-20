package com.kakao.together.service.payment.internal;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.entity.payment.PaymentType;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.exception.payment.PaymentCompleteException;
import com.kakao.together.exception.payment.PaymentFailException;
import com.kakao.together.service.donation.DonationService;
import com.kakao.together.service.payment.helper.MerchantUidProviderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentInternalHandlerImpl implements PaymentInternalHandler {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MerchantUidProviderImpl merchantUidProviderImpl;
    private final DonationService donationService;

    @Override
    @Transactional
    public void completePayment(PaymentResponse payment) {

        PaymentType type = null;
        try {
            type = merchantUidProviderImpl.extractPaymentType(payment.getMerchantUid());
        } catch (IllegalArgumentException e) {
            throw new PaymentCompleteException("유효하지 않은 상태로 인한 요청 처리 실패", e);
        }

        PaymentTransaction paymentTransaction = null;

        if (type.equals(PaymentType.DONATION)) {
            Long donationId = Long.valueOf(merchantUidProviderImpl.extractKey(payment.getMerchantUid()));
            paymentTransaction = donationService.completeDonation(donationId).getPaymentTransaction();
        }

        paymentTransaction.completePayment(payment.getImpUid(), payment.getPaidAt(), payment.getPgProvider());
    }

    @Override
    public void failPayment(PaymentResponse payment) {
        PaymentType type = null;
        try {
            type = merchantUidProviderImpl.extractPaymentType(payment.getMerchantUid());
        } catch (IllegalArgumentException e) {
            throw new PaymentFailException("유효하지 않은 상태로 인한 요청 처리 실패", e);
        }

        PaymentTransaction paymentTransaction = null;

        if (type.equals(PaymentType.DONATION)) {
            Long donationId = Long.valueOf(merchantUidProviderImpl.extractKey(payment.getMerchantUid()));
            paymentTransaction = donationService.failDonation(donationId).getPaymentTransaction();
        }

        paymentTransaction.failPayment(payment.getFailReason(), payment.getFailedAt());
    }
}
