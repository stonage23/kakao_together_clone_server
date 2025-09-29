package com.kakao.together.domain.entity.donation;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 최초 생성 이후 status 컬럼에 수정이 발생할 때에만 업데이트된다.
 */
@Entity
@Table(indexes = @Index(name = "idx_updated_at_status", columnList = "updated_at, status"))
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO test에서만
@Getter
public class Donation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DonationStatus status;
    @Column(nullable = false)
    private Long amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_id", nullable = false)
    private Fundraising fundraising;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationType type;
    private LocalDateTime completedAt;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", unique = true, nullable = true)
    private PaymentTransaction paymentTransaction;

    /**
     * Donation에 대한 PaymentTransaction은 단 1개. PaymentTransaction은 수정제한
     */
    public void completeDonation() {

        if (this.status == DonationStatus.COMPLETE) throw new CustomException(ErrorCode.ALREADY_COMPLETE_DONATION);

        if (this.status != DonationStatus.PENDING) {
            throw new IllegalStateException("must be PENDING state before complete donation");
        }
        if (this.paymentTransaction == null) {
            throw new IllegalStateException("payment transaction is null");
        }
        this.status = DonationStatus.COMPLETE;
        this.completedAt = LocalDateTime.now();
    }

    public void linkPaymentTransaction(PaymentTransaction paymentTransaction) {
        if (this.status != DonationStatus.PENDING) {
            throw new IllegalStateException("must be PENDING state before link payment transaction");
        }
        if (this.paymentTransaction != null) {
            throw new IllegalStateException("payment transaction is already linked");
        }
        this.paymentTransaction = paymentTransaction;
    }

    public void requestDonationCancel() {
        this.status = DonationStatus.REQUEST_CANCEL;
    }

    public void cancelDonation() {
        executeCancel();
    }

    public void cancelByUser() {

        if (this.status == DonationStatus.CANCELLED) {
            throw new IllegalStateException("donation is already cancelled");
        }

        if (this.status != DonationStatus.COMPLETE && this.status != DonationStatus.FAILED_CANCEL) {
            throw new IllegalStateException("'COMPLETE' or 'FAILED_CANCEL' state can be cancelled");
        }

        if (!this.fundraising.isOngoing()) {
            throw new IllegalStateException("only ongoing fundraising is allowed");
        }
        if (ChronoUnit.DAYS.between(this.getCreatedAt(), LocalDateTime.now()) > 7) {
            throw new IllegalStateException("cannot cancel donation because cancellation date is greater than 7 days");
        }

        executeCancel();
    }

    public void cancelByAdmin() {
        executeCancel();
    }

    private void executeCancel() {
        if (this.status == DonationStatus.CANCELLED) {
            throw new IllegalStateException("donation is already cancel");
        }
        this.status = DonationStatus.CANCELLED;
    }

    public void failCancelDonation() {
        this.status = DonationStatus.FAILED_CANCEL;
        this.paymentTransaction.failCancel();
    }

    public void failDonation() {
        this.status = DonationStatus.FAILED;
    }

    public void forceCompleteDonation() {
        if (this.paymentTransaction == null) {
            throw new IllegalStateException("payment transaction is null");
        }
        this.status = DonationStatus.COMPLETE;
    }
}
