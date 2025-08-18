package com.kakao.together.domain.entity.donation;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.payment.PaymentTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Donation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DonationStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_id", nullable = false)
    private Fundraising fundraising;
    @Enumerated(EnumType.STRING)
    private DonationType type;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "payment_transaction_id", unique = true, nullable = true)
    private PaymentTransaction paymentTransaction;

    public void updateStatus(DonationStatus status) {
        this.status = status;
    }
}
