package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.image.Image;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.post.Post;
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

    public interface ContentCommand {
        Content toEntity(Post post);
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ImageContentCommand implements ContentCommand{
        private String caption;
        private Image image;
        private Integer order;
        private Post post;

        @Override
        public ImageContent toEntity(Post post) {
            return ImageContent.builder()
                    .caption(this.caption)
                    .image(this.image)
                    .order(this.order)
                    .post(post)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SubtitleContentCommand implements ContentCommand{
        private String subtitle;
        private Integer order;
        private Post post;

        @Override
        public SubTitleContent toEntity(Post post) {
            return SubTitleContent.builder()
                    .subtitle(this.subtitle)
                    .order(this.order)
                    .post(post)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class TextContentCommand implements ContentCommand{
        private String text;
        private Integer order;
        private Post post;

        @Override
        public TextContent toEntity(Post post) {
            return TextContent.builder()
                    .text(this.text)
                    .post(post)
                    .order(this.order)
                    .build();
        }
    }
}
