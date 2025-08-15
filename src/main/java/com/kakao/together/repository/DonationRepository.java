package com.kakao.together.repository;

import com.kakao.together.domain.entity.donation.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
