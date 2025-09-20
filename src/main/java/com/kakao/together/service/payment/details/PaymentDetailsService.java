package com.kakao.together.service.payment.details;

import com.kakao.together.controller.paymentgate.dto.PaymentDetails;

public interface PaymentDetailsService {
    PaymentDetails loadPaymentByMerchantUid(String merchantUid);
}
