package com.kakao.together.service.paymentgate;

import com.kakao.together.controller.paymentgate.dto.PaymentDetails;
import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.api.paymentgate.exception.PaymentNotFoundException;

public interface PaymentDetailsService {
    PaymentDetails loadPaymentByMerchantUid(String merchantUid) throws PaymentNotFoundException;
    default void completePayment(PaymentResponse payment) {}
    default void updatePaymentCancellation(String merchantUid) {}
}

