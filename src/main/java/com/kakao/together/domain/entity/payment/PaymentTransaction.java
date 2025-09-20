package com.kakao.together.domain.entity.payment;

import com.kakao.together.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * PaymentTransaction은 불변
 * merchantUid으로 결제 카테고리 분류
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentTransaction extends BaseTimeEntity {

    @Builder
    public PaymentTransaction(String merchantUid, Long amount) {
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_transaction_id")
    private Long id;
    private String impUid;
    private String merchantUid;
    @Column(precision = 15, scale = 2)
    private Long amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String pgProvider;
    private Instant paidAt;
    private Instant cancelledAt;
    private Instant failedAt;
    private String failReason;

    public void completePayment(String impUid, Long paidAt, String pgProvider) {
        this.status = PaymentStatus.APPROVAL;
        this.pgProvider = pgProvider;
        this.impUid = impUid;
        this.paidAt = Instant.ofEpochSecond(paidAt);
    }

    public void cancelPayment(Long cancelledAt) {
        this.status = PaymentStatus.CANCEL;
        this.cancelledAt = Instant.ofEpochSecond(cancelledAt);
    }

    public void failPayment(String failReason, Long failedAt) {
        this.status = PaymentStatus.FAILED;
        this.failReason = failReason;
        this.failedAt = Instant.ofEpochSecond(failedAt);
    }

    public void failCancel() {
        this.status = PaymentStatus.FAILED_CANCEL;
    }
}
