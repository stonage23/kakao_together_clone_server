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
                this.contents.add(content);
            }
        }
    }
}
