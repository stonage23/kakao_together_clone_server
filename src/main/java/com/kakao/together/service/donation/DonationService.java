package com.kakao.together.service.donation;

import com.kakao.together.controller.dto.DonationDto.DonationRequest;
import com.kakao.together.domain.entity.donation.Donation;

public interface DonationService {
    void createDonation(DonationRequest request, Long donorId);

    Donation getDonationEntity(Long donationId);

    void cancelDonationById(Long donationId);
}
