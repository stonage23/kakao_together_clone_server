package com.kakao.together.external.paymentgate.web.dto;

import com.kakao.together.domain.entity.payment.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;


public class DefaultPaymentDetails implements PaymentDetails {

    private String merchantUid;
    private BigDecimal amount;
    private PaymentStatus status;

    @Builder
    public DefaultPaymentDetails(String merchantUid, BigDecimal amount, PaymentStatus status) {
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String getMerchantUid() {
        return this.merchantUid;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public boolean isRequiredVerification() {
        return this.status == PaymentStatus.PENDING;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DefaultPaymentDetails paymentDetails) {
            return this.merchantUid.equals(paymentDetails.getMerchantUid());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "DefaultPaymentDetails{" +
                "merchantUid='" + merchantUid + '\'' +
                ", amount=" + amount +
                '}';
    }

//    public static class DefaultPaymentDetailsBuilder {
//        private DefaultPaymentDetailsBuilder () {}
//
//        private String merchantUid;
//        private BigDecimal amount;
//
//        public DefaultPaymentDetailsBuilder merchantUid(String merchantUid) {
//            this.merchantUid = merchantUid;
//            return this;
//        }
//
//        public DefaultPaymentDetailsBuilder amount(BigDecimal amount) {
//            this.amount = amount;
//            return this;
//        }
//
//        public DefaultPaymentDetails build() {
//            return new DefaultPaymentDetails(merchantUid, amount);
//        }
//    }
//
//    public static DefaultPaymentDetailsBuilder builder() {
//        return new DefaultPaymentDetailsBuilder();
//    }
}
