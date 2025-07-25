package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.document.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class ContentDto {

    private Long documentId;
    private String contentType;
    private String subtitle;
    private String text;
    private String caption;
    private Image image;
    private Integer order;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ImageContentDto {
        private String caption;
        private Image image;
        private Integer order;
        private Post post;

        public ImageContent toEntity() {
            return ImageContent.builder()
                    .caption(this.caption)
                    .image(this.image)
                    .order(this.order)
                    .document(post)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SubtitleContentDto {
        private String subtitle;
        private Integer order;
        private Post post;

        public SubTitleContent toEntity() {
            return SubTitleContent.builder()
                    .subtitle(this.subtitle)
                    .order(this.order)
                    .document(this.post)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class TextContentDto {
        private String text;
        private Integer order;
        private Post post;

        public TextContent toEntity() {
            return TextContent.builder()
                    .text(this.text)
                    .document(this.post)
                    .order(this.order)
                    .build();
        }
    }
}
