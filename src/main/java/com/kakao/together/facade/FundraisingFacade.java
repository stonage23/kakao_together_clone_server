package com.kakao.together.facade;

import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.controller.dto.ContentDto.SubtitleContentDto;
import com.kakao.together.controller.dto.ContentDto.TextContentDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto;
import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.document.Post;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.ContentService;
import com.kakao.together.service.ImageService;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import static com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;

@Service
@RequiredArgsConstructor
public class FundraisingFacade {

    private FundraisingService fundraisingService;
    private ImageService imageService;
    private ContentService contentService;

    private static final String SUBTITILE_TAG = "h2";
    private static final String TEXT_TAG = "p";
    private static final String IMAGE_TAG = "img";

    @Transactional
    public void createTempFundraising(EditFundraisingDto requestDto) {
        Fundraising fundraising = fundraisingService.createTempFundraising(requestDto);

        buildPost(buildElements(requestDto.getHtml()), fundraising.getPost());
    }

    @Transactional
    public void updateTempFundraising(EditFundraisingDto requestDto) {
        Fundraising fundraising = fundraisingService.getFundraisingEntity(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING, "작성중인 글을 찾을 수 없습니다."));

        buildPost(buildElements(requestDto.getHtml()), fundraising.getPost());

    }

    @Transactional
    public void createFundraising(EditFundraisingDto requestDto) {
        Fundraising fundraising;
        if (requestDto.getFundraisingId() == null)
            fundraising = fundraisingService.createFundraising(requestDto);
        else
            fundraising = fundraisingService.transTempToUpload(requestDto);

        buildPost(buildElements(requestDto.getHtml()), fundraising.getPost());
    }

    @Transactional
    public void updateFundraising(EditFundraisingDto requestDto) {
        Fundraising fundraising = fundraisingService.getFundraisingEntity(requestDto.getFundraisingId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING)
        );

        buildPost(buildElements(requestDto.getHtml()), fundraising.getPost());
    }

    private Elements buildElements(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        return doc.body().children();
    }

    /**
     * 이미지를 제외한 콘텐츠(text, subtitle)은 불러올 때 DB에서 삭제
     * @param elements
     * @param post
     */
    // TODO Image 처리 수정 필요해보임
    private void buildPost(Elements elements, Post post) {
        AtomicInteger order = new AtomicInteger(0);

        elements.forEach(element -> {
                    int currentOrder = order.getAndIncrement();
                    switch (element.tagName()) {
                        case SUBTITILE_TAG -> contentService.createSubtitleContent(SubtitleContentDto.builder()
                                .subtitle(element.text())
                                .post(post)
                                .order(currentOrder)
                                .build());
                        case TEXT_TAG -> contentService.createTextContent(TextContentDto.builder()
                                .text(element.text())
                                .post(post)
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
                                    .post(post)
                                    .build());
                        }
                        default -> throw new CustomException(ErrorCode.NOT_VALID_TAG);
                    }
                }
        );
    }

    public FundraisingResponse getFundraising(Long id) {
        return fundraisingService.findFundraising(id);
    }
}
