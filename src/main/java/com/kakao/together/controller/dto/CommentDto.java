package com.kakao.together.controller.dto;

import com.kakao.together.controller.dto.MemberDto.Writer;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class CommentDto {

    private CommentDto () {}

    @Builder
    @AllArgsConstructor
    @Getter
    public static class CommentRequest {
        private Long memberId;
        private Long fundraisingId;
        private String comment;

        public Comment toEntity(Member writer, Fundraising fundraising) {
            return Comment.builder()
                    .writer(writer)
                    .fundraising(fundraising)
                    .comment(this.comment)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class CommentResponse {
        private Long id;
        private Writer writer;
        private Long fundraisingId;
        private String comment;

        public CommentResponse fromEntity(Comment comment) {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .writer(Writer.fromEntity(comment.getWriter(), comment.getWriter().getProfile()))
                    .fundraisingId(comment.getFundraising().getId())
                    .comment(comment.getComment())
                    .build();
        }
    }
}
