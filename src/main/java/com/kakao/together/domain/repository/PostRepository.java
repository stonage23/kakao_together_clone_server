package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
