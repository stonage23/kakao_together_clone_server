package com.kakao.together.paymentgate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PortOnePaymentResponse {
    private String getMerchantUid;
    private BigDecimal price;
}
