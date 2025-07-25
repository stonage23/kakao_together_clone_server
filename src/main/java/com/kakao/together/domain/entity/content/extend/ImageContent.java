package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.document.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@DiscriminatorValue("IMAGE")
@Getter
public class ImageContent extends Content {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "image_id")
    private Image image;

    private String caption;

    @Builder
    public ImageContent(Post post, Integer order, Image image, String caption) {
        this.setContentType(ContentType.IMAGE);
        this.setOrder(order);
        this.setPost(post);
        this.image = image;
        this.caption = caption;
    }
}
