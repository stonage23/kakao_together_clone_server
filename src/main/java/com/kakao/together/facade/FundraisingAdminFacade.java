package com.kakao.together.facade;

import com.kakao.together.controller.dto.ContentDto.ContentCommand;
import com.kakao.together.controller.dto.ContentDto.SubtitleContentCommand;
import com.kakao.together.controller.dto.ContentDto.TextContentCommand;
import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleEditFundraisingResponse;
import com.kakao.together.domain.entity.image.Image;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.content.ContentService;
import com.kakao.together.service.image.ImageService;
import com.kakao.together.service.agency.AgencyService;
import com.kakao.together.service.file.FileService;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kakao.together.controller.dto.ContentDto.ImageContentCommand;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundraisingAdminFacade {

    private final FundraisingService fundraisingService;
    private final ImageService imageService;
    private final ContentService contentService;
    private final AgencyService agencyService;
    private final PostService postService;
    private final FileService<ImageDto> imageFileService;

    private static final String SUBTITILE_TAG = "h2";
    private static final String TEXT_TAG = "p";
    private static final String IMAGE_TAG = "img";

    @Transactional
    public void createTempFundraising(EditFundraisingDto requestDto) {

        List<ContentCommand> contentCommands = elementsToContents(buildElements(requestDto.getHtml()));

        Long createdPostId = postService.buildPost(requestDto.getPostId(), contentCommands);

        checkImageContentsChange(requestDto.getHtml(), createdPostId);

        fundraisingService.createTempFundraising(requestDto, createdPostId);
    }

    @Transactional
    // TODO 임시 주석 해결
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

        Fundraising fundraising = fundraisingService.findFundraisingNullable(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING, "작성중인 글을 찾을 수 없습니다."));

//        Post createdPost = elementsToContents(buildElements(requestDto.getHtml()), requestDto.getPostId());
//
//        checkImageContentsChange(requestDto.getHtml(), createdPost);
//
//        fundraising.updateFundraising(requestDto, agency, thumbnail, createdPost);
    }

    @Transactional
    // TODO 임시 주석 해결
    public void createFundraising(EditFundraisingDto requestDto) {

        Agency agency = agencyService.getAgencyEntityById(requestDto.getAgencyId());
        Image thumbnail = imageService.getImageEntityById(requestDto.getThumbnail().getImageId());

        if (requestDto.getFundraisingId() != null)
            fundraisingService.deleteIfExists(requestDto.getFundraisingId());

//        Post createdPost = elementsToContents(buildElements(requestDto.getHtml()), requestDto.getPostId());
//
//        checkImageContentsChange(requestDto.getHtml(), createdPost);
//
//        fundraisingService.createFundraising(requestDto.toEntity(agency, thumbnail, createdPost));
    }

    @Transactional
    // TODO 임시 주석 해결
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

        Fundraising fundraising = fundraisingService.findFundraisingNullable(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING, "작성중인 글을 찾을 수 없습니다."));

//        Post createdPost = elementsToContents(buildElements(requestDto.getHtml()), requestDto.getPostId());
//
//        checkImageContentsChange(requestDto.getHtml(), createdPost);
//
//        fundraising.updateFundraising(requestDto, agency, thumbnail, createdPost);
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
    // TODO Elements을 각 Command으로 나누고 post서비스로 전달 후 저장 로직은 애플리케이션 서비스에서 책임
    private List<ContentCommand> elementsToContents(Elements elements) {

        AtomicInteger order = new AtomicInteger(0);
        List<ContentCommand> contentCommands = new ArrayList<>();

        elements.forEach(element -> {
                    int currentOrder = order.getAndIncrement();
                    switch (element.tagName()) {
                        case SUBTITILE_TAG -> {
                            SubtitleContentCommand subtitleContentCommand = SubtitleContentCommand.builder()
                                .subtitle(element.text())
//                                .post(finalCreatedPost)
                                .order(currentOrder)
                                    .build();
                            contentCommands.add(subtitleContentCommand);
                        }
                        case TEXT_TAG -> {
                            TextContentCommand textContentCommand = TextContentCommand.builder()
                                .text(element.text())
//                                .post(finalCreatedPost)
                                .order(currentOrder)
                                    .build();
                            contentCommands.add(textContentCommand);
                        }
                        case IMAGE_TAG -> {
                            Image image = imageService.createIfSrcNotExist(parseImageTag(element));
                            String caption = element.attr("caption");
                            ImageContentCommand imageContentCommand = ImageContentCommand.builder()
                                    .image(image)
                                    .caption(caption)
                                    .order(currentOrder)
//                                    .post(finalCreatedPost)
                                    .build();
                            contentCommands.add(imageContentCommand);
                        }
                        default -> throw new CustomException(ErrorCode.NOT_VALID_TAG);
                    }
                }
        );

        return contentCommands;
    }

    public ImageDto parseImageTag(Element element) {
        return ImageDto.builder()
                .realName(element.attr("realName"))
                .originalName(element.attr("originalName"))
                .url((element.attr("src")))
                .build();
    }

    public List<SimpleEditFundraisingResponse> getTempFundraisings() {
        return fundraisingService.findAllTempFundraisings().stream()
                .map(SimpleEditFundraisingResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public EditFundraisingDto findTemporaryFundraisingById(Long id) {
        Fundraising fundraising = fundraisingService.findTempFundraisingById(id);
        String buildedHtml = renderHtmlAndDeleteContents(fundraising.getPost());
        return EditFundraisingDto.fromEntity(fundraising, buildedHtml);
    }

    private String renderHtmlAndDeleteContents(Post post) {
        String buildedHtml = postService.postToHtml(post);
        deleteContentsInPost(post);
        return buildedHtml;
    }

    private void deleteContentsInPost(Post post) {
        post.getContents().forEach(content -> {
            if (content instanceof SubTitleContent subtitleContent) {
                contentService.deleteContent(subtitleContent.getId());
            } else if (content instanceof TextContent textContent) {
                contentService.deleteContent(textContent.getId());
            } else if (content instanceof ImageContent imageContent) {
                contentService.deleteContent(imageContent.getId());
            } else throw new CustomException(ErrorCode.NOT_VALID_TAG, "허용하지 않는 유형의 Post content 태그");
        });
    }

    private void checkImageContentsChange(String html, Long postId) {
        Set<Long> removedIds = extractRemovedImageIds(html, postId);
        deleteImagesByIds(removedIds);
    }

    private Set<Long> extractRemovedImageIds(String html, Long postId) {
        Set<Long> currentIds = Jsoup.parseBodyFragment(html)
                .select("img")
                .stream()
                .map(img -> Long.valueOf(img.attr("imageId")))
                .collect(Collectors.toSet());

        Set<Long> previousIds = contentService.getImageContentsByPost(postId)
                .stream()
                .map(ic -> ic.getImage().getId())
                .collect(Collectors.toSet());

        previousIds.removeAll(currentIds);
        return previousIds;
    }

    private void deleteImagesByIds(Set<Long> imageIds) {
        for (Long id : imageIds) {
            ImageContent imgContent = contentService.findImageContentByImageId(id);
            if (imgContent != null) {
                if (imgContent.getImage() != null) {
                    imageFileService.deleteFile(imgContent.getImage().getUrl());
                }
                contentService.deleteContent(imgContent.getId());
            } else {
                imageService.findImageById(id).ifPresent(image -> {
                    imageService.delete(image);
                    imageFileService.deleteFile(image.getUrl());
                });
            }
        }
    }
}
