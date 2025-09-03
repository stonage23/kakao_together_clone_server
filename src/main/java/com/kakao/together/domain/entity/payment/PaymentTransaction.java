package com.kakao.together.domain.entity.payment;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String impUid;
    private String merchantUid;
    @Column(precision = 15, scale = 2)
    private Long amount;
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

    public void setImpUid(String impUid) {
        if (this.impUid != null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "impUid 상태 충돌; impUid는 최초 1회만 값을 넣을 수 있습니다.");
        }
        this.impUid = impUid;
    }
}
