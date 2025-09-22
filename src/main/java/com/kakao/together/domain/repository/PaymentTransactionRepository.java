package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.payment.PaymentStatus;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByMerchantUid(String merchantUid);

    Optional<PaymentTransaction> findByMerchantUidAndStatus(String merchantUid, PaymentStatus paymentStatus);
}
