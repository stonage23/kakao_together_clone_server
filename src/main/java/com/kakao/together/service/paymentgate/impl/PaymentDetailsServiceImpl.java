package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.external.paymentgate.web.dto.DefaultPaymentDetails;
import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.external.paymentgate.service.PaymentDetailsService;
import com.kakao.together.service.payment.transaction.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    private final PaymentTransactionService paymentTransactionService;

    @Override
    public PaymentDetails loadPaymentByMerchantUid(String merchantUid) {

        PaymentTransaction paymentTransaction = null;

        try {
            paymentTransaction = paymentTransactionService.getPaymentTransaction(merchantUid);
        } catch (NoSuchElementException e) {
            throw e;
        }

        return DefaultPaymentDetails.builder()
                .merchantUid(paymentTransaction.getMerchantUid())
                .amount(BigDecimal.valueOf(paymentTransaction.getAmount()))
                .status(paymentTransaction.getStatus())
                .build();
    }
}
