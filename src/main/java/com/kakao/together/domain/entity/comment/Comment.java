package com.kakao.together.domain.entity.comment;

import com.kakao.together.controller.dto.CommentDto.CommentUpdateRequest;
import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_id")
    private Fundraising fundraising;

    private String comment;

    public void updateComment(CommentUpdateRequest updateRequest) {
        this.comment = updateRequest.getComment();
    }
}
