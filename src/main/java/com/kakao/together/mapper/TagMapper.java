package com.kakao.together.mapper;

import com.kakao.together.api.htmlparser.RawTag;
import com.kakao.together.controller.dto.ContentDto.ImageContentCommand;
import com.kakao.together.controller.dto.ContentDto.SubtitleContentCommand;
import com.kakao.together.controller.dto.ContentDto.TextContentCommand;
import com.kakao.together.controller.image.dto.ImageCommand;

public class TagMapper {

    public static ImageCommand toImageCommand(RawTag element) {
        return ImageCommand.builder()
                .savedName(element.getAttributes().get("realName"))
                .originalName(element.getAttributes().get("originalName"))
                .url((element.getAttributes().get("src")))
                .build();
    }

    public static ImageContentCommand toImageContentCommand(RawTag element) {
        return ImageContentCommand.builder()
                .imageId(Long.valueOf(element.getAttributes().get("imageId")))
                .caption(element.getAttributes().get("caption"))
                .build();
    }

    public static SubtitleContentCommand toSubtitleContentCommand(RawTag element) {
        return SubtitleContentCommand.builder()
                .subtitle(element.getText())
                .build();
    }

    public static TextContentCommand toTextContentCommand(RawTag element) {
        return TextContentCommand.builder()
                .text(element.getText())
                .build();
    }
}
