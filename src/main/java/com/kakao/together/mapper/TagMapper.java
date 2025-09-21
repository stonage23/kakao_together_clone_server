package com.kakao.together.mapper;

import com.kakao.together.controller.post.dto.RawTag;
import com.kakao.together.controller.post.dto.ContentDto.ImageContentCommand;
import com.kakao.together.controller.post.dto.ContentDto.SubtitleContentCommand;
import com.kakao.together.controller.post.dto.ContentDto.TextContentCommand;
import com.kakao.together.controller.image.dto.ImageCommand;

public class TagMapper {

    public static ImageCommand toImageCommand(RawTag element) {
        return ImageCommand.builder()
                .savedName(element.getAttributes().get("realName"))
                .originalName(element.getAttributes().get("originalName"))
                .url((element.getAttributes().get("src")))
                .build();
    }

    public static ImageContentCommand toImageContentCommand(RawTag element, int order) {
        return ImageContentCommand.builder()
                .imageId(Long.valueOf(element.getAttributes().get("imageid")))
                .caption(element.getAttributes().get("caption"))
                .order(order)
                .build();
    }

    public static SubtitleContentCommand toSubtitleContentCommand(RawTag element, int order) {
        return SubtitleContentCommand.builder()
                .subtitle(element.getText())
                .order(order)
                .build();
    }

    public static TextContentCommand toTextContentCommand(String text, int order) {
        return TextContentCommand.builder()
                .text(text)
                .order(order)
                .build();
    }
}
