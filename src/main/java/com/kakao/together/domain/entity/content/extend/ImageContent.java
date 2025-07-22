package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.content.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@DiscriminatorValue("IMAGE")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageContent extends Content {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "image_id")
    private Image image;

    private String caption;
}
