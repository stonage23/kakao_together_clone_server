package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
