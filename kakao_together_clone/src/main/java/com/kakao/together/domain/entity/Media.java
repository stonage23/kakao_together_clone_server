package com.kakao.together.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table (name = "Media")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Media <T>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private String id;

    // TODO DB인덱싱(Media 속성이 어느 카테고리에 포함된건지)을 위한 필드
    private String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_id", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T fundraising;

    private String filePath;
    private String description;
}
