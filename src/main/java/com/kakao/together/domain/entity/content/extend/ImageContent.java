package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@DiscriminatorValue("IMAGE")
@Getter
public class ImageContent extends Content {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_info_id")
    private FileInfo image;

    private String caption;

    @Builder
    public ImageContent(Post post, Integer order, FileInfo image, String caption) {
        this.setContentType(ContentType.IMAGE);
        this.setOrderIndex(order);
        this.setPost(post);
        this.image = image;
        this.caption = caption;
    }
}
