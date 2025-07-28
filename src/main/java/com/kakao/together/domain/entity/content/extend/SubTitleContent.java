package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.post.Post;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SUBTITLE")
@NoArgsConstructor
@Getter
public class SubTitleContent extends Content {
    private String subtitle;

    @Builder
    public SubTitleContent(Integer order, String subtitle, Post post) {
        this.setContentType(ContentType.SUBTITLE);
        this.setOrderIndex(order);
        this.setPost(post);
        this.subtitle = subtitle;
    }
}
