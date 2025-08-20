package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    boolean existsByMemberIdAndFundraisingIdAndStatusAndType(Long memberId, Long fundraisingId, DonationStatus status, DonationType type);

    List<Donation> findAllByMemberId(Long memberId);
}
