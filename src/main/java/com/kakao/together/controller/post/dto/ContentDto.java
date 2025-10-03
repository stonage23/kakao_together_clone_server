package com.kakao.together.controller.post.dto;

import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        private static class ImageValue {
            private String url;
            private String caption;
        }

        public static ContentResponse fromText(TextContent content) {
            return ContentResponse.builder()
                    .type(content.getType().getValue())
                    .value(content.getText())
                    .build();
        }

        public static ContentResponse fromImage(ImageContent content, String url) {
            return ContentResponse.builder()
                    .type(content.getType().getValue())
                    .value(new ImageValue(url, content.getCaption()))
                    .build();
        }

        public static ContentResponse fromSubtitle(SubTitleContent content) {
            return ContentResponse.builder()
                    .type(content.getType().getValue())
                    .value(content.getSubtitle())
                    .build();
        }
    }
}
