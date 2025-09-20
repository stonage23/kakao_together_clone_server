package com.kakao.together.service.member.impl;

import com.kakao.together.controller.auth.dto.AuthDto.DeleteMemberRequest;
import com.kakao.together.controller.auth.dto.AuthDto.ResetPasswordRequest;
import com.kakao.together.controller.member.dto.MemberDto.DonationStateResponse;
import com.kakao.together.controller.member.dto.MemberDto.MeDetailResponse;
import com.kakao.together.controller.member.dto.MemberDto.ProfileUpdateRequest;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.file.FileStatus;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.member.MemberStatus;
import com.kakao.together.domain.entity.member.Profile;
import com.kakao.together.domain.repository.DonationRepository;
import com.kakao.together.domain.repository.FileInfoRepository;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.FileStorageService;
import com.kakao.together.service.file.impl.FilePathResolver;
import com.kakao.together.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    @Override
    @Transactional
    public void createMember(SignupByEmailRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent((member) -> {
                    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
                });
        Member member = request.toEntity();
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
    }

    @Override
    public boolean isExistsEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        return passwordEncoder.matches(password, member.getPassword());
    }

    @Override
    @Transactional
    public void updatePassword(ResetPasswordRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(
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

        member.updateMemberStatus(MemberStatus.DELETED);
    }

    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByProfile_Nickname(nickname);
    }

    @Override
    public MeDetailResponse getMyDetail(String username) {
        Member member = memberRepository.findById(Long.valueOf(username)).orElseThrow(
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
            image.updateFileStatus(FileStatus.USED);
        }
        FileInfo preImage = member.getProfile().getProfileImage();
        if (preImage != null) {
            fileInfoRepository.delete(preImage);
            try {
                fileStorageService.deleteFile(preImage.getSavedName(), preImage.getContentType());
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FAILED_DELETE_FILE);
            }
        }

        member.updateProfile(request.getNickname(), image, request.getBirth(), request.getAddress());
    }

    @Override
    public DonationStateResponse getDonationState(Long memberId) {
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

        return DonationStateResponse.builder()
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
}
