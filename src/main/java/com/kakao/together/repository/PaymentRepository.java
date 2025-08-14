package com.kakao.together.repository;

import com.kakao.together.payment.PaymentStatus;
import com.kakao.together.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByMerchantUid(String merchantUid);

    Optional<PaymentTransaction> findByMerchantUidAndStatus(String merchantUid, PaymentStatus paymentStatus);
}
