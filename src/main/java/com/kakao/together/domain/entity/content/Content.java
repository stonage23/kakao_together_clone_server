package com.kakao.together.domain.entity.content;

import com.kakao.together.domain.entity.document.Document;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

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
    @JoinColumn(name = "document_id")
    private Document document;

    private Integer order;
}