package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.api.paymentgate.exception.PaymentNotFoundException;
import com.kakao.together.controller.paymentgate.dto.DefaultPaymentDetails;
import com.kakao.together.controller.paymentgate.dto.PaymentDetails;
import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.domain.entity.payment.CardPaymentTransactionDetail;
import com.kakao.together.domain.entity.payment.PaymentStatus;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.service.paymentgate.PaymentDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPaymentDetailsService implements PaymentDetailsService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentDetails loadPaymentByMerchantUid(String merchantUid) throws PaymentNotFoundException {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByMerchantUid(merchantUid).orElseThrow(
                () -> new PaymentNotFoundException("요청하신 merchantUid에 해당하는 결제 내역이 존재하지 않습니다.")
        );

        return DefaultPaymentDetails.builder()
                .merchantUid(paymentTransaction.getMerchantUid())
                .amount(BigDecimal.valueOf(paymentTransaction.getAmount()))
                .build();
    }

    @Override
    @Transactional
    public void completePayment(PaymentResponse payment) {
        Optional<PaymentTransaction> optional = paymentTransactionRepository.findByMerchantUid(payment.getMerchantUid());

        if (optional.isEmpty()) {
            log.error("[결제완료실패] merchantUid에 해당하는 결제정보가 DB에 없음; merchantUid={}", payment.getMerchantUid());
            return;
        }

        PaymentTransaction paymentTransaction = optional.get();
        paymentTransaction.updateStatus(PaymentStatus.APPROVAL);
        paymentTransaction.setImpUid(payment.getImpUid());

        switch (payment.getPayMethod()) {
            case "card" ->
                    paymentTransaction.setPaymentTransactionDetail(CardPaymentTransactionDetail.fromPaymentResponse(payment));
        }
    }

    @Override
    @Transactional
    public void updatePaymentCancellation(String merchantUid) {
        paymentTransactionRepository.findByMerchantUid(merchantUid).ifPresentOrElse(
                paymentTransaction -> paymentTransaction.updateStatus(PaymentStatus.CANCEL),
                () -> log.error("[결제취소실패] merchantUid에 해당하는 결제정보가 DB에 없음; merchantUid={}", merchantUid)
        );
    }
}
