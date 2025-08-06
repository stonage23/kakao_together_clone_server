package com.kakao.together.repository;

import com.kakao.together.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMerchantUid(String merchantUid);
}
