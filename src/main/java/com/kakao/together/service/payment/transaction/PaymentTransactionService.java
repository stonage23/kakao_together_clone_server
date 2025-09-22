package com.kakao.together.service.payment.transaction;


import com.kakao.together.domain.entity.payment.PaymentTransaction;

public interface PaymentTransactionService {

    PaymentTransaction getPaymentTransaction(String merchnatUid);
    void savePaymentAsPending(String merchantUid, Long amount);
    void cancelPayment(Long paymentTransactionId, Long cancelledAt);
}
