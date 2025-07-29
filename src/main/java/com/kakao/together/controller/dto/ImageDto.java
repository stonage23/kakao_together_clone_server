package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ImageDto {

    private Long imageId;
    private String originalName;
    private String realName;
    private String url;

    public static ImageDto fromEntity(Image image) {
        return ImageDto.builder()
                .imageId(image.getId())
                .originalName(image.getOriginalName())
                .realName(image.getRealName())
                .url(image.getUrl())
                .build();
    }

    public Image toEntity() {
        return Image.builder()
                .originalName(this.originalName)
                .realName(this.realName)
                .url(this.url)
                .build();
    }
}
