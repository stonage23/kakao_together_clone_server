package com.kakao.together.domain.entity.content;

import com.kakao.together.domain.entity.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "content_type")
@Getter
@ToString
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "content_type", insertable = false, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType type;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Integer orderIndex;

    protected void setContentType(ContentType contentType) {
        this.type = contentType;
    }

    protected void setOrderIndex(Integer order) {
        this.orderIndex = order;
    }

    protected void setPost(Post post) {
        this.post = post;
    }
}