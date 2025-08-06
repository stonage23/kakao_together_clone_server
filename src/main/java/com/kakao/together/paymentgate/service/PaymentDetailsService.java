package com.kakao.together.paymentgate.service;

import com.kakao.together.paymentgate.PaymentDetails;
import com.kakao.together.paymentgate.exception.PaymentNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface PaymentDetailsService {
    PaymentDetails loadPaymentByMerchantUid(String merchantUid) throws PaymentNotFoundException;
    default void updatePaymentApproval(String merchantUid) {}
    default void updatePaymentCancellation(String merchantUid) {}
}

