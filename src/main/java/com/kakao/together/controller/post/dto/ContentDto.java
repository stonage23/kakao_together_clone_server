package com.kakao.together.controller.post.dto;

import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class ContentDto {

    public interface ContentCommand {
        void setOrder(Integer order);
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ImageContentCommand implements ContentCommand{
        private String caption;
        private Long imageId;
        private Integer order;
        private Post post;

        public ImageContent toEntity(Post post, FileInfo image) {
            return ImageContent.builder()
                    .caption(this.caption)
                    .image(image)
                    .order(this.order)
                    .post(post)
                    .build();
        }

        @Override
        public void setOrder(Integer order) {
            this.order = order;
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SubtitleContentCommand implements ContentCommand{
        private String subtitle;
        private Integer order;
        private Post post;

        public SubTitleContent toEntity(Post post) {
            return SubTitleContent.builder()
                    .subtitle(this.subtitle)
                    .order(this.order)
                    .post(post)
                    .build();
        }

        @Override
        public void setOrder(Integer order) {
            this.order = order;
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class TextContentCommand implements ContentCommand{
        private String text;
        private Integer order;
        private Post post;

        public TextContent toEntity(Post post) {
            return TextContent.builder()
                    .text(this.text)
                    .post(post)
                    .order(this.order)
                    .build();
        }

        @Override
        public void setOrder(Integer order) {
            this.order = order;
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ContentResponse {
        private String type;
        private Object value;

        @AllArgsConstructor
        private static class ImageValue {
            private String url;
            private String caption;
        }

        public static ContentResponse fromText(String type, String value) {
            return ContentResponse.builder()
                    .type(type)
                    .value(value)
                    .build();
        }

        public static ContentResponse fromImage(String type, String url, String caption) {
            return ContentResponse.builder()
                    .type(type)
                    .value(new ImageValue(url, caption))
                    .build();
        }

        public static ContentResponse fromSubtitle(String type, String value) {
            return ContentResponse.builder()
                    .type(type)
                    .value(value)
                    .build();
        }
    }
}
