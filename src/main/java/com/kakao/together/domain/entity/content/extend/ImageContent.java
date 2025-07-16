package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.content.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Entity
@DiscriminatorValue("IMAGE")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageContent extends Content {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ImageContent_Image", joinColumns = @JoinColumn(name = "content_id"))
    private Set<Image> images = new HashSet<>();

    @Embeddable
    public static class Image {
        private String url;
        private String originalName;
        private String realName;
    }
}
