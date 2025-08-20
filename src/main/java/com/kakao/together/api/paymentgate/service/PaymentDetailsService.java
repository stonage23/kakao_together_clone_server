package com.kakao.together.api.paymentgate.service;

import com.kakao.together.api.paymentgate.PaymentDetails;
import com.kakao.together.api.paymentgate.PaymentResponse;
import com.kakao.together.api.paymentgate.exception.PaymentNotFoundException;

public interface PaymentDetailsService {
    PaymentDetails loadPaymentByMerchantUid(String merchantUid) throws PaymentNotFoundException;
    default void completePayment(PaymentResponse payment) {}
    default void updatePaymentCancellation(String merchantUid) {}
}

