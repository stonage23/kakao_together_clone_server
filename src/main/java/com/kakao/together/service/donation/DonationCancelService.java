package com.kakao.together.service.donation;

import org.springframework.transaction.annotation.Transactional;

public interface DonationCancelService {
    void cancelByMember(Long memberId, Long donationId);

    @Transactional
    void cancelByAdmin(Long donationId);
}
