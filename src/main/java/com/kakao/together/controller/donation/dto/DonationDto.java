package com.kakao.together.controller.donation.dto;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingSummaryResponse;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DonationDto {

    private DonationDto () {}

    @Builder
    @AllArgsConstructor
    @Getter
    public static class DonationCompleteRequest {
        private String impUid;
        private String merchantUid;
        private Long amount;
        private Long fundraisingId;
        private String type;

        public Donation toEntity(Member donor, Fundraising fundraising, PaymentTransaction paymentTransaction) {
            DonationType donationType = switch (this.type) {
                case "DIRECT" -> DonationType.DIRECT;
                case "INDIRECT" -> DonationType.COMMENT;
                default -> throw new CustomException(ErrorCode.INVALID_ARGUMENT, "적절하지 않은 DonationType: " + this.type);
            };
            return Donation.builder()
                    .member(donor)
                    .fundraising(fundraising)
                    .status(DonationStatus.COMPLETE)
                    .paymentTransaction(paymentTransaction)
                    .type(donationType)
                    .amount(amount)
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DonationCreateWithCommentWrapper {
        private CommentRequest commentRequest;
        private CommentDonationRequest donationRequest;
    }

    @AllArgsConstructor
    @Getter
    public static class CommentDonationRequest {
        private Long fundraisingId;
    }

    @Builder
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
        private String paymentUrl;

        public static DonationPendingResponse fromEntity(Donation donation, String paymentUrl) {
            return DonationPendingResponse.builder()
                    .donationId(donation.getId())
                    .paymentUrl(paymentUrl)
                    .build();
        }
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
