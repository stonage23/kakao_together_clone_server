package com.kakao.together.domain.entity.comment;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import jakarta.persistence.*;

@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_id")
    private Fundraising fundraising;

    private String comment;
}
