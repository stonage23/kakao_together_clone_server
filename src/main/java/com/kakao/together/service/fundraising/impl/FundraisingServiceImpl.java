package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.domain.entity.image.Image;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.repository.AgencyRepository;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.domain.repository.ImageRepository;
import com.kakao.together.domain.repository.PostRepository;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundraisingServiceImpl implements FundraisingService {

    private final FundraisingRepository fundraisingRepository;
    private final AgencyRepository agencyRepository;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    /**
     * Post는 생성된 후 id만 전달
     * @param requestDto
     * @param postId
     */
    @Override
    public void createTempFundraising(EditFundraisingDto requestDto, Long postId) {
        Agency agency = null;
        Image thumbnail = null;

        if (requestDto.getAgencyId() != null) {
            agency = agencyRepository.findById(requestDto.getAgencyId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; agencyId: " + requestDto.getAgencyId())
            );
        }

        if (requestDto.getThumbnail() != null && requestDto.getThumbnail().getImageId() != null) {
            thumbnail = imageRepository.findById(requestDto.getThumbnail().getImageId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; imageId: " + requestDto.getThumbnail().getImageId())
            );
        }

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; postId: " + postId)
        );

        fundraisingRepository.save(requestDto.toEntity(agency, thumbnail, post));
    }

    // TODO Post 업데이트 방식이 곤란하군..
    @Transactional
    public void updateFundraisingPost(Long fundraisingId, Post post) {
        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId)
        );

        Post fudraisingPost = fundraising.getPost();

        if (fudraisingPost == null) {
            fundraising.setPost(post);
        } else {
//            fudraisingPost.updatePost(post);
        }
    }

    @Override
    public Fundraising createFundraising(Fundraising fundrasing) {
        return fundraisingRepository.save(fundrasing);
    }

    @Override
    public Optional<Fundraising> findFundraisingNullable(Long fundraisingId) {
        return fundraisingRepository.findById(fundraisingId);
    }

    @Override
    public Fundraising findFundraisingNullCheck(Long fundraisingId) {
        return fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다.")
        );
    }

    @Override
    public List<FundraisingResponse> findFundraisingsExpiringInThreeDaysLimit(int limit) {
        return fundraisingRepository.findFundraisingsWithExpiringInThreeDaysLimit(limit).stream()
                .map(fundraising -> FundraisingResponse.fromEntity(fundraising))
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsTopLimit(int limit) {
        return fundraisingRepository.findFundraisingsWithTopLimit(limit).stream()
                .map(fundraising -> FundraisingResponse.fromEntity(fundraising))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteIfExists(Long fundraisingId) {
        if (!fundraisingRepository.existsById(fundraisingId)) {
            log.warn("존재하지 않는 모금Id로 데이터 삭제 시도; fundraisingId: {}, method: {}", fundraisingId, "FundraisingServiceImpl.existsById");
            return;
        }
        fundraisingRepository.deleteById(fundraisingId);
    }

    @Override
    public FundraisingResponse getOngoingFundraisingResponse(Long fundraisingId) {
        return fundraisingRepository.findByIdAndFundraisingStatus(fundraisingId, FundraisingStatus.ONGOING)
                .map(FundraisingResponse::fromEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다."));
    }

    @Override
    public Fundraising findTempFundraisingById(Long fundraisingId) {
        return fundraisingRepository.findByIdAndFundraisingStatus(fundraisingId, FundraisingStatus.TEMPORARY).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "임시 저장 모금이 존재하지 않습니다.")
        );
    }

    @Override
    public List<Fundraising> findAllTempFundraisings() {
        return fundraisingRepository.findByFundraisingStatus(FundraisingStatus.TEMPORARY);
    }
}
