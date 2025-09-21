package com.kakao.together.service.donation;

public interface DonationCancelService {
    void cancelByMember(Long memberId, Long donationId);

    void cancelByAdmin(Long donationId);
}
