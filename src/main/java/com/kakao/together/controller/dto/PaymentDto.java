package com.kakao.together.controller.dto;

import com.kakao.together.payment.PaymentTransaction;
import com.kakao.together.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

public class PaymentDto {

    @AllArgsConstructor
    @Getter
    public static class PaymentPendingDto {
        private String merchantUid;
        private BigDecimal amount;

        public PaymentTransaction toEntity() {
            return PaymentTransaction.builder()
                    .merchantUid(this.merchantUid)
                    .amount(this.amount)
                    .status(PaymentStatus.PENDING)
                    .build();
        }

    }
}
