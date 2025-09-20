package com.kakao.together.controller.donation.dto;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingSummaryResponse;
import com.kakao.together.domain.entity.donation.Donation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DonationDto {

    private DonationDto () {}

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationCompleteRequest {
        private Long donorId;
        private Long donationId;

        public boolean isValid(Long donationId) {
            return this.donationId == donationId;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationCreateWithCommentWrapper {
        private CommentRequest commentRequest;
        private CommentDonationRequest donationRequest;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class CommentDonationRequest {
        private Long fundraisingId;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationsResponse {
        private Long id;
        private String status;
        private String type;
        private Long amount;
        private FundraisingSummaryResponse fundraising;

        public static DonationsResponse fromEntity(Donation donation) {
            return DonationsResponse.builder()
                    .id(donation.getId())
                    .status(donation.getStatus().getValue())
                    .type(donation.getType().getValue())
                    .amount(donation.getPaymentTransaction().getAmount())
                    .fundraising(FundraisingSummaryResponse.fromEntity(donation.getFundraising()))
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationPendingResponse {

        private Long donationId;
        private String merchantUid;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DonationPendingRequest {
        private Long fundraisingId;
        private Long amount;
    }
}
