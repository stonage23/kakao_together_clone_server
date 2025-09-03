package com.kakao.together.controller.payment.dto;

import com.kakao.together.domain.entity.payment.CardPaymentTransactionDetail;
import com.kakao.together.domain.entity.payment.PaymentStatus;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

public class PaymentDto {

    @AllArgsConstructor
    @Getter
    public static class PaymentPendingRequest {
        private String merchantUid;
        private Long amount;

        public PaymentTransaction toEntity() {
            return PaymentTransaction.builder()
                    .merchantUid(this.merchantUid)
                    .amount(this.amount)
                    .status(PaymentStatus.PENDING)
                    .build();
        }

    }

    public abstract static class PaymentTransactionDetailResponse {}

    @Builder
    @AllArgsConstructor
    @Getter
    public static class CardPaymentTransactionDetailResponse extends PaymentTransactionDetailResponse{
        private String cardCode;
        private String cardName;
        private String cardNumber;
        private Integer cardType;
        private Instant paidAt;
        private Instant failAt;
        private Instant cancelledAt;
        public static CardPaymentTransactionDetailResponse fromEntity(CardPaymentTransactionDetail paymentTransactionDetail) {
            return CardPaymentTransactionDetailResponse.builder()
                    .cardCode(paymentTransactionDetail.getCardCode())
                    .cardName(paymentTransactionDetail.getCardName())
                    .cardNumber(paymentTransactionDetail.getCardNumber())
                    .cardType(paymentTransactionDetail.getCardType())
                    .paidAt(paymentTransactionDetail.getPaidAt())
                    .failAt(paymentTransactionDetail.getFailAt())
                    .cancelledAt(paymentTransactionDetail.getCancelledAt())
                    .build();
        }
    }
}
