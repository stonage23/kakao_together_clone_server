package com.kakao.together.payment;

import com.kakao.together.paymentgate.PaymentResponse;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@DiscriminatorValue("CARD")
@NoArgsConstructor
@Getter
public class CardPaymentTransactionDetail extends PaymentTransactionDetail {

    private String cardCode;
    private String cardName;
    private String cardNumber;
    private Integer cardType;
    private Instant paidAt;
    private Instant failAt;
    private Instant canccelledAt;


    @Builder
    public CardPaymentTransactionDetail(String cardCode, String cardName, String cardNumber, Integer cardType, Instant paidAt, Instant failAt, Instant canccelledAt) {
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.paidAt = paidAt;
        this.failAt = failAt;
        this.canccelledAt = canccelledAt;
    }

    public static CardPaymentTransactionDetail fromPaymentResponse(PaymentResponse paymentResponse) {
        return CardPaymentTransactionDetail.builder()
                .cardCode(paymentResponse.getCardCode())
                .cardName(paymentResponse.getCardName())
                .cardNumber(paymentResponse.getCardNumber())
                .cardType(paymentResponse.getCardType())
                .paidAt(paymentResponse.getPaidAt())
                .failAt(paymentResponse.getFailAt())
                .canccelledAt(paymentResponse.getCancelledAt())
                .build();
    }
}
