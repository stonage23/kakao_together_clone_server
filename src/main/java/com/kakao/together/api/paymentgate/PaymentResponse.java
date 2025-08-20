package com.kakao.together.api.paymentgate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@AllArgsConstructor
@Getter
public class PaymentResponse {
    private String merchantUid;
    private String impUid;
    private BigDecimal amount;
    private String payMethod;
    private String pgProvider;
    private String bankCode;
    private String bankName;
    private String cardCode;
    private String cardName;
    private String cardNumber;
    private int cardType;
    private String vbankCode;
    private String vbankName;
    private String vbankNum;
    private String vbankHolder;
    private Instant vbankDate;
    private Instant vbankIssuedAt;
    private String currency;
    private String buyerName;
    private String buyerEmail;
    private String buyerTel;
    private Instant paidAt;
    private Instant failAt;
    private Instant cancelledAt;
}
