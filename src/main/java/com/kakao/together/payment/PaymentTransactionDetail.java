package com.kakao.together.payment;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "payment_type")
public abstract class PaymentTransactionDetail {

    @Id
    @Column(name = "payment_transaction_detail_id", unique = true)
    private Long id;
}
