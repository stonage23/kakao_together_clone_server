package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.document.Post;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Entity
@DiscriminatorValue("TEXT")
@NoArgsConstructor
@Getter
public class TextContent extends Content {

    @Lob
    private String text;

    @Builder
    public TextContent(Integer order, String text, Post post) {
        this.setContentType(ContentType.TEXT);
        this.setOrder(order);
        this.setPost(post);
        this.text = text;
    }
}
