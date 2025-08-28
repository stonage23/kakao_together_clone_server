package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.dto.ContentDto.ContentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.*;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.repository.*;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.impl.FilePathResolver;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundraisingServiceImpl implements FundraisingService {

    private final FundraisingRepository fundraisingRepository;
    private final AgencyRepository agencyRepository;
    private final FileInfoRepository fileInfoRepository;
    private final PostRepository postRepository;
    private final FilePathResolver filePathResolver;
    private final CommentRepository commentRepository;

    /**
     * Post는 생성 책임이 있는 로직에서 생성 후 id만 전달
     * @param request
     * @param postId
     */
    @Override
    @Transactional
    public Long createFundraising(EditFundraisingRequest request, Long postId) {

        Agency agency = null;
        FileInfo thumbnail = null;

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; postId: " + postId)
        );

        if (request.getAgencyId() != null) {
            agency = agencyRepository.findById(request.getAgencyId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; agencyId: " + request.getAgencyId())
            );
        }

        if (request.getThumbnailId() != null) {
            thumbnail = fileInfoRepository.findById(request.getThumbnailId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; imageId: " + request.getThumbnailId())
            );
        } else {
            post.getContents().stream()
                    .filter(content -> content.getType().equals(ContentType.IMAGE))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.BUISINESS_VIOLATION, "이미지를 최소 1개 이상 첨부해주세요."));
            thumbnail = fileInfoRepository.findById(request.getThumbnailId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; imageId: " + request.getThumbnailId())
            );
        }

        Fundraising createdFundraising = fundraisingRepository.save(request.toEntity(agency, thumbnail, post));

        return createdFundraising.getId();
    }

    @Override
    @Transactional
    public Long updateFundraising(EditFundraisingRequest request) {

        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + request.getFundraisingId())
        );

        Agency agency = null;
        FileInfo thumbnail = null;

        if (request.getAgencyId() != null) {
            agency = agencyRepository.findById(request.getAgencyId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; agencyId: " + request.getAgencyId())
            );
        }

        if (request.getThumbnailId() != null) {
            thumbnail = fileInfoRepository.findById(request.getThumbnailId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; imageId: " + request.getThumbnailId())
            );
        } else {
            fundraising.getPost().getContents().stream()
                    .filter(content -> content.getType().equals(ContentType.IMAGE))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.BUISINESS_VIOLATION, "이미지를 최소 1개 이상 첨부해주세요."));
            thumbnail = fileInfoRepository.findById(request.getThumbnailId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; imageId: " + request.getThumbnailId())
            );
        }

        fundraising.updateFundraising(request, agency, thumbnail);

        return fundraising.getId();
    }

    @Override
    @Transactional
    public void updateFundraisingStatus(Long fundraisingId, String status) {

        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId)
        );

        fundraising.updateFundraisingStatus(status);
    }

    @Override
    public List<FundraisingResponse> findFundraisingsExpiringInThreeDaysLimit(int limit) {
        return fundraisingRepository.findFundraisingsWithExpiringInThreeDaysLimit(limit).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsTopLimit(int limit) {
        return fundraisingRepository.findFundraisingsWithTopLimit(limit).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    private FundraisingResponse resolveFundraisingResponse(Fundraising fundraising) {
        FileInfo image = fundraising.getThumbnail();
        String thumbnailUrl = filePathResolver.resolveUploadPath(image.generateFilename(), image.getContentType()).toString();
        return FundraisingResponse.builder()
                .id(fundraising.getId())
                .title(fundraising.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .targetAmount(fundraising.getTargetAmount())
                .startDate(fundraising.getStartDate())
                .endDate(fundraising.getEndDate())
                .fundraisingStatus(fundraising.getFundraisingCurrent())
                .agency(AgencyDto.fromEntity(fundraising.getAgency()))
                .currentAmount(fundraising.getFundraisingCurrent().getCurrentAmount())
                .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                .build();
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
                .map(this::resolveFundraisingResponse)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다."));
    }

    @Override
    public EditFundraisingResponse findTempFundraisingById(Long fundraisingId) {
        Fundraising fundraising = fundraisingRepository.findByIdAndDraftStatus(fundraisingId, DraftStatus.TEMPORARY)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "임시 저장 모금이 존재하지 않습니다."));
        List<ContentResponse> contents = contentResponseListMapper(fundraising.getPost().getContents());
        return EditFundraisingResponse.fromEntity(fundraising, contents);
    }

    @Override
    public List<SimpleTempFundraisingResponse> findAllTempFundraisings() {
        return fundraisingRepository.findByFundraisingStatus(DraftStatus.TEMPORARY).stream()
                .map(SimpleTempFundraisingResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDraftToCreated(EditFundraisingRequest request) {
        Long updatedFundraising = updateFundraising(request);
        Fundraising fundraising = fundraisingRepository.findById(updatedFundraising).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "임시 저장 모금이 존재하지 않습니다.")
        );
        fundraising.updateDraftToCreated();
    }

    @Override
    public FundraisingPostResponse getFundraisingStory(Long fundraisingId) {

        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다.")
        );

        Post post = fundraising.getPost();
        List<ContentResponse> contents = contentResponseListMapper(post.getContents());

        return FundraisingPostResponse.builder()
                .postId(post.getId())
                .postType(post.getType().getValue())
                .contents(contents)
                .build();
    }

    @Override
    public List<CommentResponse> findAllComments(Long fundraisingId) {
        List<Comment> comments = commentRepository.findAllByFundraisingId(fundraisingId);

        return comments.stream().map(
                        comment -> {
                            FileInfo profileImage = comment.getWriter().getProfile().getProfileImage();
                            String profileImageUrl = filePathResolver.resolveUploadPath(profileImage.generateFilename(), profileImage.getContentType()).toString();
                            return CommentResponse.fromEntity(comment, profileImageUrl);
                        }
                )
                .collect(Collectors.toList());
    }

    private List<ContentResponse> contentResponseListMapper(List<Content> contents) {
        return contents.stream()
                .map(content -> {
                    if (content instanceof SubTitleContent subTitleContent)
                        return ContentResponse.fromSubtitle(ContentType.SUBTITLE.getValue(), subTitleContent.getSubtitle());
                    else if (content instanceof TextContent textContent)
                        return ContentResponse.fromText(ContentType.TEXT.getValue(), textContent.getText());
                    else if (content instanceof ImageContent imageContent) {
                        FileInfo image = imageContent.getImage();
                        String url = filePathResolver.resolveUploadPath(image.generateFilename(), image.getContentType()).toString();
                        return ContentResponse.fromImage(ContentType.IMAGE.getValue(), url, imageContent.getCaption());
                    } else throw new CustomException(ErrorCode.NOT_PERMITTED_CONDITION);
                }).collect(Collectors.toList());
    }
}
