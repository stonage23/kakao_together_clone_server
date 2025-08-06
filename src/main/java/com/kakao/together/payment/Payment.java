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
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
    private String merchantUid;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}
