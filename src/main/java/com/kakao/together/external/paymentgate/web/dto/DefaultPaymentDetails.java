package com.kakao.together.external.paymentgate.web.dto;

import java.math.BigDecimal;


public class DefaultPaymentDetails implements PaymentDetails {

    private String merchantUid;
    private BigDecimal amount;

    public DefaultPaymentDetails(String merchantUid, BigDecimal amount) {
        this.merchantUid = merchantUid;
        this.amount = amount;
    }

    @Override
    public String getMerchantUid() {
        return this.merchantUid;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
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

    public static class DefaultPaymentDetailsBuilder {
        private DefaultPaymentDetailsBuilder () {}

        private String merchantUid;
        private BigDecimal amount;

        public DefaultPaymentDetailsBuilder merchantUid(String merchantUid) {
            this.merchantUid = merchantUid;
            return this;
        }

        public DefaultPaymentDetailsBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DefaultPaymentDetails build() {
            return new DefaultPaymentDetails(merchantUid, amount);
        }
    }

    public static DefaultPaymentDetailsBuilder builder() {
        return new DefaultPaymentDetailsBuilder();
    }
}
