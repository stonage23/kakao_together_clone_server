package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFundraisingId(Long fundraisingId);
}
