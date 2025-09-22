package com.kakao.together.external.paymentgate.web.dto;

import java.math.BigDecimal;

public interface PaymentDetails {

    String getMerchantUid();

    BigDecimal getAmount();

    default boolean isApprovalPayment() {
        return true;
    }
}
