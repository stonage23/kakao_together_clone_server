package com.kakao.together.service.member.impl;

import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStatusResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.Profile;
import com.kakao.together.domain.repository.DonationRepository;
import com.kakao.together.domain.repository.FileInfoRepository;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.event.MemberSignupCompleteEvent;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.external.redis.exception.RedisServiceException;
import com.kakao.together.service.cache.CacheService;
import com.kakao.together.service.file.FileStorageService;
import com.kakao.together.service.file.impl.FilePathResolver;
import com.kakao.together.service.mail.MailService;
import com.kakao.together.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileInfoRepository fileInfoRepository;
    private final FilePathResolver filePathResolver;
    private final FileStorageService fileStorageService;
    private final DonationRepository donationRepository;
    private final MailService mailService;
    private final CacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String EMAIL_PREFIX = "email ";

    private void createMember(SignupByEmailRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent((member) -> {
                    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
                });
        Member member = request.toEntity();
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
    }

    @Override
    public void checkEmailDuplication(String email) {

        boolean isPresent = memberRepository.existsByEmail(email);
        if (isPresent) throw new CustomException(ErrorCode.CONFLICT_EXCEPTION, "이미 존재하는 이메일입니다.");
    }

    @Override
    @Transactional
    public void updatePassword(ResetPasswordRequest request) {

        String email = cacheService.getData(EMAIL_PREFIX + request.getCode());

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    @Transactional
    public void deleteMember(Long memberId, DeleteMemberRequest requestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        try {
            member.deleteMember();
        } catch (IllegalStateException e) {
            log.warn("유저 요청에 의한 삭제 처리중 문제 발생: 적절하지 않은 상태");
            throw new CustomException(ErrorCode.CONFLICT_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void checkNicknameDuplication(String nickname) {
        boolean isPresent = memberRepository.existsByProfile_Nickname(nickname);

        if (isPresent) throw new CustomException(ErrorCode.CONFLICT_EXCEPTION, "이미 존재하는 닉네임입니다.");
    }

    @Override
    public MeDetailResponse getMyDetail(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        Profile profile = member.getProfile();
        if (profile.getProfileImage() == null) {
            String defaultUrl = "기본 프로필 이미지 경로";
            return MeDetailResponse.fromEntity(member, defaultUrl);
        } else {
            FileInfo profileImage = profile.getProfileImage();
            String url = filePathResolver.resolveUploadPath(profileImage.getSavedName(), profileImage.getContentType()).toString();
            return MeDetailResponse.fromEntity(member, url);
        }
    }

    @Override
    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest request) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        FileInfo image = null;
        if (request.getImageId() != null) {
            image = fileInfoRepository.findById(request.getImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "업로드되어 있어야할 이미지가 존재하지 않음; imageId: " + request.getImageId()));
            image.updateStatusToUsed();
        }
        FileInfo preImage = member.getProfile().getProfileImage();
        if (preImage != null) {
            fileInfoRepository.delete(preImage);
            fileStorageService.deleteFile(preImage.getSavedName(), preImage.getContentType());
            throw new CustomException(ErrorCode.FAILED_DELETE_FILE);
        }

        member.updateProfile(request.getNickname(), image, request.getBirth(), request.getAddress());
    }

    @Override
    public DonationStatusResponse getMyTotalDonationStatus(Long memberId) {
        if (!memberRepository.existsById(memberId))
            throw new CustomException(ErrorCode.NOT_FOUND_USER);

        List<Donation> donations = donationRepository.findAllByMemberIdAndStatus(memberId, DonationStatus.COMPLETE.getValue());

        Long directDonationAmount = donations.stream()
                .filter(donation -> donation.getType() == DonationType.DIRECT)
                .mapToLong(donation -> donation.getAmount()).sum();
        Long directDonationCount = donations.stream()
                .filter(donation -> donation.getType() == DonationType.DIRECT)
                .count();
        Long indirectDonationAmount = donations.stream()
                .filter(donation -> donation.getType() == DonationType.COMMENT)
                .mapToLong(donation -> donation.getAmount()).sum();
        Long commentDonationCount = donations.stream()
                .filter(donation -> donation.getType() == DonationType.COMMENT)
                .count();
        Long donationAmount = directDonationAmount + indirectDonationAmount;
        Long donationCount = directDonationCount + commentDonationCount;

        return DonationStatusResponse.builder()
                .donationAmount(donationAmount)
                .donationCount(donationCount)
                .directDonationCount(directDonationCount)
                .directDonationAmount(directDonationAmount)
                .indirectDonationAmount(indirectDonationAmount)
                .commentDonationCount(commentDonationCount)
                .build();
    }

    @Override
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("not present member"));
    }

    @Override
    @Transactional
    public void handleSignupRequest(SignupByEmailRequest request) {

        if (memberRepository.existsByEmail(request.getEmail()))
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        createMember(request);

        String code = mailService.sendSignupMail(request.getEmail());

        cacheService.setData(generateEmailKey(code), request.getEmail(), 60 * 30);
    }

    @Override
    @Transactional
    public void activateMember(String code) {

        String email = null;

        try {
            email = cacheService.getData(generateEmailKey(code));
        } catch (RedisServiceException e) {
            log.error("신규 멤버 계정 활성화를 위한 캐시에서 인증 코드 조회 중 문제발생; 캐시 서버에 문제가 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (email == null) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        try {
            member.activateMember();
        } catch (IllegalStateException e) {
            log.warn("이미 활성화된 멤버 활성화 시도; memberId={}", member.getId());
            throw new CustomException(ErrorCode.CONFLICT_EXCEPTION, e.getMessage());
        }

        eventPublisher.publishEvent(new MemberSignupCompleteEvent(generateEmailKey(code)));
    }

    private String generateEmailKey(String key) {
        return EMAIL_PREFIX + key;
    }
}
