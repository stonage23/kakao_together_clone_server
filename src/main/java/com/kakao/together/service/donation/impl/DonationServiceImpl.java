package com.kakao.together.service.donation.impl;

import com.kakao.together.controller.donation.dto.DonationDto.CommentDonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.repository.DonationRepository;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationServiceImpl implements DonationService {

    private final MemberRepository memberRepository;
    private final FundraisingRepository fundraisingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final DonationRepository donationRepository;

    @Value("${together.donation.comment.amount}")
    private Long commentDonationAmount;

    @Override
    @Transactional
    public void createDonation(DonationRequest request, Long donorId) {

        Member doner = memberRepository.findById(donorId).orElseThrow(
                () -> {
                    log.error("전달 받은 donorId와 일치하는 유저 정보 없음; donorId: {}", donorId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "기부자 정보 DB에서 조회 실패");
                }
        );
        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId()).orElseThrow(
                () -> {
                    log.error("전달 받은 fundraisingId와 일치하는 모금 정보 없음; fundraisingId: {}", request.getFundraisingId());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "모금 정보 DB에서 조회 실패");
                }
        );
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByMerchantUid(request.getMerchantUid()).orElseThrow(
                () -> {
                    log.error("전달 받은 merchantUid와 일치하는 결제 내역 없음; merchantUid: {}", request.getMerchantUid());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "결제 내역 DB에서 조회 실패");
                }
        );

        Donation donation = request.toEntity(doner, fundraising, paymentTransaction);

        donationRepository.save(donation);
    }

    @Override
    public Donation getDonationEntity(Long donationId) {
        return donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
    }

    @Override
    public void cancelDonationById(Long donationId) {
        Donation donation = donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
        donation.updateStatus(DonationStatus.CANCELLED);
    }

    @Override
    public boolean isPresentValidDonation(Long fundraisingId, Long memberId, DonationType type) {
        return donationRepository.existsByMemberIdAndFundraisingIdAndStatusAndType(memberId, fundraisingId, DonationStatus.COMPLETE, type);
    }

    @Override
    @Transactional
    public void createCommentDonation(Long donorId, CommentDonationRequest donationCreateRequest) {
        Fundraising fundraising = fundraisingRepository.findById(donationCreateRequest.getFundraisingId()).orElseThrow(
                () -> {
                    log.error("전달 받은 fundraisingId와 일치하는 모금 정보 없음; fundraisingId: {}", donationCreateRequest.getFundraisingId());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "모금 정보 DB에서 조회 실패");
                }
        );

        Member doner = memberRepository.findById(donorId).orElseThrow(
                () -> {
                    log.error("전달 받은 donorId와 일치하는 유저 정보 없음; donorId: {}", donorId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "기부자 정보 DB에서 조회 실패");
                }
        );

        Donation donation = Donation.builder()
                .fundraising(fundraising)
                .status(DonationStatus.COMPLETE)
                .type(DonationType.COMMENT)
                .member(doner)
                .amount(commentDonationAmount)
                .build();
        donationRepository.save(donation);
    }

    @Override
    public List<DonationsResponse> getAllDonationsForDonor(Long donorId) {
        List<Donation> donations = donationRepository.findAllByMemberId(donorId);
        return donations.stream().map(DonationsResponse::fromEntity).collect(Collectors.toList());
    }
}
