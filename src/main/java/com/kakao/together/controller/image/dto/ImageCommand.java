package com.kakao.together.controller.image.dto;

import com.kakao.together.file.domain.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ImageCommand {

    private Long id;
    private String originalName;
    private String savedName;
    private String url;

    public static ImageCommand fromEntity(FileInfo image) {
        return ImageCommand.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .savedName(image.getSavedName())
                .build();
    }

    public FileInfo toEntity() {
        return FileInfo.builder()
                .originalName(this.originalName)
                .savedName(this.savedName)
                .build();
    }
}
