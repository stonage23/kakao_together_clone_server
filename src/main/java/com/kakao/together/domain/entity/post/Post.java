package com.kakao.together.domain.entity.post;

import com.kakao.together.domain.entity.content.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex asc")
    private List<Content> contents = new ArrayList<>();

    public void updatePost(List<Content> contents) {
        this.contents.clear();
        if (contents != null) {
            for (Content content : contents) {
                // 연관관계의 주인이 아닌 쪽(Post)에서 객체를 추가하면서,
                // 주인 쪽(Content)에도 참조를 설정해준다 (연관관계 편의 메서드).
                this.contents.add(content);
            }
        }
    }
}
