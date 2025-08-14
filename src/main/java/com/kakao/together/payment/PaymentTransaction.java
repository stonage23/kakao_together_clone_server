package com.kakao.together.payment;

import com.kakao.together.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PaymentTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_transaction_id")
    private Long id;
    private String merchantUid;
    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "payment_transaction_detail_id")
    private PaymentTransactionDetail paymentTransactionDetail;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentTransactionDetail (PaymentTransactionDetail paymentTransactionDetail) {
        this.paymentTransactionDetail = paymentTransactionDetail;
    }
}
