package com.kakao.together.external.paymentgate.service;

import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;

public interface PaymentDetailsService {
    PaymentDetails loadPaymentByMerchantUid(String merchantUid);
}
