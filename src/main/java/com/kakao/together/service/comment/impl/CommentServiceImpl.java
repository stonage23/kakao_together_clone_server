package com.kakao.together.service.comment.impl;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.comment.dto.CommentDto.CommentUpdateRequest;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.repository.CommentRepository;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final FundraisingRepository fundraisingRepository;

    @Override
    @Transactional
    public void createComment(Long writerId, CommentRequest requestDto) {
        Member writer = memberRepository.findById(writerId).orElseThrow(
                () -> {
                    log.error("존재해야하는 유저 엔티티가 존재하지 않음; memberId: {}", writerId);
                    return new CustomException(ErrorCode.NOT_FOUND_USER);
                }
        );
        Fundraising fundraising = fundraisingRepository.findById(requestDto.getFundraisingId()).orElseThrow(
                () -> {
                    log.error("존재해야하는 모금 엔티티가 존재하지 않음; fundraisingId: {}", requestDto.getFundraisingId());
                    return new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING);
                }
        );

        commentRepository.save(requestDto.toEntity(writer, fundraising));
    }

    @Override
    @Transactional
    public void updateComment(Long authenticatedMemberId, Long commentId, CommentUpdateRequest requestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("존재해야하는 댓글 엔티티가 존재하지 않음; commentId: {}", commentId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "수정할 댓글이 존재하지 않습니다.");
                }
        );
        Member writer = memberRepository.findById(comment.getWriter().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        if (!writer.getId().equals(authenticatedMemberId)) {
            log.warn("인증받은 유저가 다른 유저가 작성한 댓글 수정 시도");
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        comment.updateComment(requestDto);
    }

    @Override
    @Transactional
    public void deleteComment(Long authenticatedMemberId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("존재해야하는 댓글 엔티티가 존재하지 않음; commentId: {}", commentId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "삭제할 댓글이 존재하지 않습니다.");
                }
        );
        Member writer = memberRepository.findById(comment.getWriter().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        if (!writer.getId().equals(authenticatedMemberId)) {
            log.warn("인증받은 유저가 다른 유저가 작성한 댓글 삭제 시도");
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        commentRepository.delete(comment);
    }
}
