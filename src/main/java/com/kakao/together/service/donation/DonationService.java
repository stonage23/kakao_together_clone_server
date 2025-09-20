package com.kakao.together.service.donation;

import com.kakao.together.controller.donation.dto.DonationDto.CommentDonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationPendingRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationType;

import java.util.List;

public interface DonationService {
    Donation completeDonation(Long donationId);

    void cancelDonation(Long donationId);

    boolean isPresentValidDonation(Long fundraisingId, Long id, DonationType donationType);

    void createCommentDonation(Long donorId, CommentDonationRequest donationCreateRequest);

    List<DonationsResponse> getAllDonationsForDonor(Long donorId);

    Donation createPendingDonation(Long donorId, DonationPendingRequest request);

    void cancelPendingDonation(Long donorId, Long donationId);

    Donation getDonation(Long donationId, Long donorId);

    void failCancelDonation(Long donationId);

    Donation getDonation(Long donationId);

    Donation failDonation(Long donationId);

    void forceCompleteDonation(Long donationId);

    void cancelByMember(Long donationId);
}
