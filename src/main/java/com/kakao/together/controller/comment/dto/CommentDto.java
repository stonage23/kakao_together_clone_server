package com.kakao.together.controller.comment.dto;

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
        private Long commentId;
        private Writer writer;
        private Long fundraisingId;
        private String comment;

        public static CommentResponse fromEntity(Comment comment, String profileUrl) {
            Member writer = comment.getWriter();
            return CommentResponse.builder()
                    .commentId(comment.getId())
                    .writer(new Writer(writer.getId(), writer.getProfile().getNickname(), profileUrl))
                    .fundraisingId(comment.getFundraising().getId())
                    .comment(comment.getComment())
                    .build();
        }

        @AllArgsConstructor
        @Getter
        private static class Writer {
            private Long id;
            private String nickname;
            private String profileUrl;
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class CommentUpdateRequest {
        private String comment;
    }
}
