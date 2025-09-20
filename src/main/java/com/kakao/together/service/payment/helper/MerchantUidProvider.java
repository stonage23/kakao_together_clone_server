package com.kakao.together.service.payment.helper;

import com.kakao.together.domain.entity.payment.MerchnatUid;
import com.kakao.together.domain.entity.payment.PaymentType;

public interface MerchantUidProvider {
    String generateMerchantUid(PaymentType paymentType, String key);

    PaymentType extractPaymentType(String merchantUid);

    String extractKey(String merchantUid);

    MerchnatUid parseMerchantUid(String merchantUid);
}
