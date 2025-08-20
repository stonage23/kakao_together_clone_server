package com.kakao.together.service.donation;

import com.kakao.together.controller.donation.dto.DonationDto.CommentDonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationType;

import java.util.List;

public interface DonationService {
    void createDonation(DonationRequest request, Long donorId);

    Donation getDonationEntity(Long donationId);

    void cancelDonationById(Long donationId);

    boolean isPresentValidDonation(Long fundraisingId, Long id, DonationType donationType);

    void createCommentDonation(Long donorId, CommentDonationRequest donationCreateRequest);

    List<DonationsResponse> getAllDonationsForDonor(Long donorId);
}
