package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.*;
import com.kakao.together.controller.post.dto.ContentDto.ContentResponse;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.repository.*;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.file.repository.FileInfoRepository;
import com.kakao.together.mapper.FundraisingMapper;
import com.kakao.together.file.resolver.ResourceUrlResolver;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ResourceUrlResolver resourceUrlResolver;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Value("${business.constants.fundraising.expiring-soon-days}")
    private Integer FUNDRAISING_EXPIRING_DAYS;

    /**
     * Post 생성 및 관리는 Post 도메인 책임. 생성 후 id만 전달
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
            ImageContent imageContent = post.getContents().
                    stream()
                    .filter(content -> content.getType().equals(ContentType.IMAGE))
                    .findFirst()
                    .map(content -> (ImageContent) content)
                    .orElseThrow(() -> new CustomException(ErrorCode.BUISINESS_VIOLATION, "이미지를 최소 1개 이상 첨부해주세요."));
            thumbnail = imageContent.getImage();
        }

        Fundraising createdFundraising = fundraisingRepository.save(request.toEntity(agency, thumbnail, post));

        return createdFundraising.getId();
    }

    @Override
    @Transactional
    public Long updateFundraising(Long fundraisingId, EditFundraisingRequest request) {

        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
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
            thumbnail = fundraising.getPost().getContents().stream()
                    .filter(content -> content.getType().equals(ContentType.IMAGE))
                    .findFirst()
                    .map(ImageContent.class::cast)
                    .orElseThrow(() -> new CustomException(ErrorCode.BUISINESS_VIOLATION, "이미지를 최소 1개 이상 첨부해주세요."))
                    .getImage();
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
    public List<FundraisingResponse> findFundraisingsExpiringInDays(int limit) {
        return fundraisingRepository.findFundraisingsWithExpiringInDaysLimit(limit, FUNDRAISING_EXPIRING_DAYS).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsTopLimit(int limit) {
        return fundraisingRepository.findFundraisingsWithTopLimit(limit).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsNearingGoal(int limit) {
        return fundraisingRepository.findFundraisingsWithNearingGoal(limit).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsOngoingRandom(int limit) {
        return fundraisingRepository.findFundraisingsWithOngoingAndNotExpired(limit).stream()
                .map(this::resolveFundraisingResponse)
                .collect(Collectors.toList());
    }

    private FundraisingResponse resolveFundraisingResponse(Fundraising fundraising) {
        FileInfo image = fundraising.getThumbnail();
        String thumbnailUrl = resourceUrlResolver.resolveUploadUrl(image.getSavedName(), image.getContentType()).toString();
        return FundraisingMapper.toFundraisingResponse(fundraising, thumbnailUrl);
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
    public FundraisingResponse findOngoingFundraising(Long fundraisingId) {
        return fundraisingRepository.findByIdAndFundraisingStatus(fundraisingId, FundraisingStatus.ONGOING)
                .map(this::resolveFundraisingResponse)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId));
    }

    @Override
    public EditFundraisingResponse findDraftFundraising(Long fundraisingId) {
        Fundraising fundraising = fundraisingRepository.findByIdAndDraftStatus(fundraisingId, DraftStatus.DRAFT)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId));
        String content = postService.resolveContent(fundraising.getPost().getId());
//        List<ContentResponse> contents = contentResponseListMapper(fundraising.getPost().getContents());
        return EditFundraisingResponse.fromEntity(fundraising, content, fundraising.getThumbnail().getId());
    }

    @Override
    public List<SimpleDraftFundraisingResponse> findAllDraftFundraisings() {
        return fundraisingRepository.findByDraftStatus(DraftStatus.DRAFT).stream()
                .map(SimpleDraftFundraisingResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    // TODO 업데이트 후 발행, 업데이트 안하고 발행 나눠야할까?
    public void updateDraftToPublished(Long fundraisingId) {
        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: "+ fundraisingId)
        );
        fundraising.updateDraftToCreated();
    }

    /**
     * 에디터 html 반환 메소드
     *
     * @param fundraisingId
     * @return
     */
    @Override
    public FundraisingPostEditResponse findFundraisingStoryHtml(Long fundraisingId) {

        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId)
        );

        Post post = fundraising.getPost();
        String content = postService.resolveContent(fundraising.getPost().getId());

        return FundraisingPostEditResponse.builder()
                .postId(post.getId())
                .postType(post.getType().getValue())
                .html(content)
                .build();
    }

    @Override
    public FundraisingPostResponse findFundraisingStory(Long fundraisingId) {
        Fundraising fundraising = fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId)
        );

        Post post = fundraising.getPost();

        List<ContentResponse> contents = new ArrayList<>();

        post.getContents().forEach(
                content -> {
                    if (content instanceof TextContent textContent) {
                        contents.add(ContentResponse.fromText(textContent));
                    } else if (content instanceof SubTitleContent subTitleContent) {
                        contents.add(ContentResponse.fromSubtitle(subTitleContent));
                    } else if (content instanceof ImageContent imageContent) {
                        FileInfo image = imageContent.getImage();
                        String url = resourceUrlResolver.resolveUploadUrl(image.getSavedName(), image.getContentType());
                        contents.add(ContentResponse.fromImage(imageContent, url));
                    }
                });
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
                            String profileImageUrl = resourceUrlResolver.resolveUploadUrl(profileImage.getSavedName(), profileImage.getContentType());
                            return CommentResponse.fromEntity(comment, profileImageUrl);
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public EditFundraisingResponse findFundraising(Long fundraisingId) {
        Fundraising fundraising = fundraisingRepository.findById(fundraisingId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; fundraisingId: " + fundraisingId));
        String content = postService.resolveContent(fundraising.getPost().getId());
        return EditFundraisingResponse.fromEntity(fundraising, content, fundraising.getThumbnail().getId());
    }
}
