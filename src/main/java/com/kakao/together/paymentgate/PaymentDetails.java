package com.kakao.together.paymentgate;

import java.math.BigDecimal;

public interface PaymentDetails {

    String getMerchantUid();

    BigDecimal getAmount();

    default boolean isApprovalPayment() {
        return true;
    }
}
