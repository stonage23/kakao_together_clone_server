package com.kakao.together.repository;

import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    boolean existsByMemberIdAndFundraisingIdAndStatusAndType(Long memberId, Long fundraisingId, DonationStatus status, DonationType type);
}
