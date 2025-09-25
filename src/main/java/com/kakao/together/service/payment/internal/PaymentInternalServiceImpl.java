package com.kakao.together.service.payment.internal;

import com.kakao.together.domain.entity.payment.MerchnatUid;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.entity.payment.PaymentType;
import com.kakao.together.exception.payment.PaymentCompleteException;
import com.kakao.together.exception.payment.PaymentFailException;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.helper.MerchantUidProvider;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentInternalServiceImpl implements PaymentInternalService {

    private final MerchantUidProvider merchantUidProvider;
    private final DonationService donationService;

    @Override
    @Transactional
    public void completePayment(PaymentResponse payment) {

        MerchnatUid merchantUidDto;
        try {
            merchantUidDto = merchantUidProvider.parseMerchantUid(payment.getMerchantUid());
        } catch (IllegalArgumentException e) {
            throw new PaymentCompleteException("유효하지 파라미터를 전달받음", e);
        }

        PaymentTransaction paymentTransaction = null;

        if (merchantUidDto.getType().equals(PaymentType.DONATION)) {
            Long donationId = Long.valueOf(merchantUidProvider.extractKey(payment.getMerchantUid()));
            paymentTransaction = donationService.completeDonation(donationId).getPaymentTransaction();
        }

        paymentTransaction.completePayment(payment.getImpUid(), payment.getPaidAt(), payment.getPgProvider());
    }

    @Override
    @Transactional
    public void failPayment(String merchantUid, String failReason, Instant failedAt) {
        PaymentType type = null;
        try {
            type = merchantUidProvider.extractPaymentType(merchantUid);
        } catch (IllegalArgumentException e) {
            throw new PaymentFailException("유효하지 않은 상태로 인한 요청 처리 실패", e);
        }

        PaymentTransaction paymentTransaction = null;

        if (type.equals(PaymentType.DONATION)) {
            Long donationId = Long.valueOf(merchantUidProvider.extractKey(merchantUid));
            paymentTransaction = donationService.failDonation(donationId).getPaymentTransaction();
        }

        paymentTransaction.failPayment(failReason, failedAt);
    }
}
