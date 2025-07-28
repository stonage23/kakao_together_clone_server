package com.kakao.together.facade;

import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.controller.dto.ContentDto.SubtitleContentDto;
import com.kakao.together.controller.dto.ContentDto.TextContentDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto;
import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.fundraising.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.entity.post.PostType;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.ContentService;
import com.kakao.together.service.ImageService;
import com.kakao.together.service.agency.AgencyService;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import static com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleEditFundraisingResponse;

@Service
@RequiredArgsConstructor
public class FundraisingFacade {

    private final FundraisingService fundraisingService;
    private final ImageService imageService;
    private final ContentService contentService;
    private final AgencyService agencyService;
    private final PostService postService;

    private static final String SUBTITILE_TAG = "h2";
    private static final String TEXT_TAG = "p";
    private static final String IMAGE_TAG = "img";

    @Transactional
    public void createTempFundraising(EditFundraisingDto requestDto) {
        Agency agency = null;
        Image thumbnail = null;

        if (requestDto.getAgencyId() != null) {
            agency = agencyService.getAgencyEntityById(requestDto.getAgencyId());
        }

        if (requestDto.getThumbnail() != null && requestDto.getThumbnail().getImageId() != null) {
            try {
                thumbnail = imageService.getImageEntityById(requestDto.getThumbnail().getImageId());
            } catch (CustomException e) {
                throw new CustomException(e.getErrorCode(), "임시저장에 사용할 이미지를 서버에서 찾을 수 없습니다.");
            }
        }

        Post createdPost = buildPost(buildElements(requestDto.getHtml()), null);

        fundraisingService.createTempFundraising(requestDto.toEntity(agency, thumbnail, createdPost));
    }

    @Transactional
    public void updateTempFundraising(EditFundraisingDto requestDto) {
        Agency agency = null;
        Image thumbnail = null;

        if (requestDto.getAgencyId() != null) {
            agency = agencyService.getAgencyEntityById(requestDto.getAgencyId());
        }

        if (requestDto.getThumbnail() != null && requestDto.getThumbnail().getImageId() != null) {
            try {
                thumbnail = imageService.getImageEntityById(requestDto.getThumbnail().getImageId());
            } catch (CustomException e) {
                throw new CustomException(e.getErrorCode(), "임시저장에 사용할 이미지를 서버에서 찾을 수 없습니다.");
            }
        }

        fundraisingService.findFundraisingNullable(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING, "작성중인 글을 찾을 수 없습니다."));

        Post createdPost = buildPost(buildElements(requestDto.getHtml()), requestDto.getPostId());

        fundraisingService.createTempFundraising(requestDto.toEntity(agency, thumbnail, createdPost));
    }

    @Transactional
    public void createFundraising(EditFundraisingDto requestDto) {

        Agency agency = agencyService.getAgencyEntityById(requestDto.getAgencyId());
        Image thumbnail = imageService.getImageEntityById(requestDto.getThumbnail().getImageId());

        if (requestDto.getFundraisingId() != null)
            fundraisingService.deleteIfExists(requestDto.getFundraisingId());

        Post createdPost = buildPost(buildElements(requestDto.getHtml()), requestDto.getPostId());

        fundraisingService.createFundraising(requestDto.toEntity(agency, thumbnail, createdPost));
    }

    @Transactional
    public void updateFundraising(EditFundraisingDto requestDto) {
        Agency agency;
        Image thumbnail;

        if (requestDto.getAgencyId() == null) {
            throw new CustomException( ErrorCode.NOT_FOUND_VALUE, "모금 업데이트에 필요한 데이터 중 누락데이터가 존재합니다; agencyId");
        }

        if (requestDto.getThumbnail() == null || requestDto.getThumbnail().getImageId() == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_VALUE, "모금 업데이트에 필요한 데이터 중 누락데이터가 존재합니다; thumbnail");
        }

        try {
            agency = agencyService.getAgencyEntityById(requestDto.getAgencyId());
            thumbnail = imageService.getImageEntityById(requestDto.getThumbnail().getImageId());
        } catch (CustomException e) {
            throw new CustomException("모금 수정 실패", e);
        }

        fundraisingService.findFundraisingNullable(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING, "해당 모금을 찾을 수 없어 수정을 완료할 수 없습니다."));

        Post createdPost = buildPost(buildElements(requestDto.getHtml()), requestDto.getPostId());

        fundraisingService.createFundraising(requestDto.toEntity(agency, thumbnail, createdPost));
    }

    private Elements buildElements(String html) {
        if (html == null) return new Elements();
        Document doc = Jsoup.parseBodyFragment(html);
        return doc.body().children();
    }

    /**
     * 이미지를 제외한 콘텐츠(text, subtitle)은 불러올 때 DB에서 삭제
     * @param elements
     */
    // TODO Image 처리 수정 필요해보임
    private Post buildPost(Elements elements, Long postId) {

        Post post;
        Post createdPost = null;

        if (postId == null) {
            post = Post.builder()
                    .type(PostType.STORY)
                    .build();
            createdPost = postService.createPost(post);
        } else {
            createdPost = postService.findPostById(postId);
        }

        final Post finalCreatedPost = createdPost;

        AtomicInteger order = new AtomicInteger(0);

        elements.forEach(element -> {
                    int currentOrder = order.getAndIncrement();
                    switch (element.tagName()) {
                        case SUBTITILE_TAG -> contentService.createSubtitleContent(SubtitleContentDto.builder()
                                .subtitle(element.text())
                                .post(finalCreatedPost)
                                .order(currentOrder)
                                .build());
                        case TEXT_TAG -> contentService.createTextContent(TextContentDto.builder()
                                .text(element.text())
                                .post(finalCreatedPost)
                                .order(currentOrder)
                                .build());
                        case IMAGE_TAG -> {
                            String src = element.attr("src");
                            Image image = imageService.createIfSrcNotExist(src);
                            String caption = element.attr("caption");
                            contentService.createImageContent(ContentDto.ImageContentDto.builder()
                                    .image(image)
                                    .caption(caption)
                                    .order(currentOrder)
                                    .post(finalCreatedPost)
                                    .build());
                        }
                        default -> throw new CustomException(ErrorCode.NOT_VALID_TAG);
                    }
                }
        );

        return createdPost;
    }

    public FundraisingResponse getOngoingFundraising(Long id) {
        return fundraisingService.getOngoingFundraisingResponse(id);
    }

    public EditFundraisingDto findTemporaryFundraisingById(Long id) {
        Fundraising fundraising = fundraisingService.findTempFundraisingById(id);
        return EditFundraisingDto.fromEntity(fundraising, postToHtml(fundraising.getPost()));
    }

    public List<FundraisingResponse> getExpiringSoonFundraisings(int limit) {
        return fundraisingService.findFundraisingsExpiringInThreeDaysLimit(limit);
    }

    public List<FundraisingResponse> getTopFundraisings(int limit) {
        return fundraisingService.findFundraisingsTopLimit(limit);
    }

    private String postToHtml(@NonNull Post post) {
        StringBuilder html = new StringBuilder();
        for (Content content : post.getContents()) {
            if (content instanceof SubTitleContent subTitleContent) {
                html.append(subTitleContent.getSubtitle());
            } else if (content instanceof TextContent textContent) {
                html.append(textContent.getText());
            } else if (content instanceof ImageContent imageContent) {
                html.append(imageContent.getImage());
            } else throw new CustomException(ErrorCode.NOT_VALID_TAG, "허용 외 타입의 콘텐츠가 DB에 저장되어 있어 HTML 구성 실패");
        }
        return html.toString();
    }

    public List<SimpleEditFundraisingResponse> getTempFundraisings() {
        return fundraisingService.findAllTempFundraisings().stream()
                .map(SimpleEditFundraisingResponse::fromEntity).collect(Collectors.toList());
    }
}
