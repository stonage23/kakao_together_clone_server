package com.kakao.together.controller.paymentgate.dto;

import java.math.BigDecimal;

public interface PaymentDetails {

    String getMerchantUid();

    BigDecimal getAmount();

    default boolean isApprovalPayment() {
        return true;
    }
}
