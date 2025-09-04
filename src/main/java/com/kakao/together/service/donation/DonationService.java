package com.kakao.together.service.donation;

import com.kakao.together.controller.donation.dto.DonationDto.*;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationType;

import java.util.List;

public interface DonationService {
    void updateDonationToComplete(String merchantUid, Long donationId);

    Donation getDonationEntity(Long donationId);

    void cancelDonationById(Long donationId);

    boolean isPresentValidDonation(Long fundraisingId, Long id, DonationType donationType);

    void createCommentDonation(Long donorId, CommentDonationRequest donationCreateRequest);

    List<DonationsResponse> getAllDonationsForDonor(Long donorId);

    DonationPendingResponse createPendingDonation(Long memberId, DonationPendingRequest request);
}
