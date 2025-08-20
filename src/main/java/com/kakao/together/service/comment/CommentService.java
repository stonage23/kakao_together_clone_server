package com.kakao.together.service.comment;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.comment.dto.CommentDto.CommentUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    void createComment(Long writerId, CommentRequest requestDto);

    void updateComment(Long authenticatedMemberId, Long commentId, CommentUpdateRequest requestDto);

    void deleteComment(Long authenticatedMemberId, Long commentId);
}
