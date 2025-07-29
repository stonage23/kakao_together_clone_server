package com.kakao.together.repository;

import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("select c from ImageContent c where c.post.id = :postId")
    List<ImageContent> findAllByTypeAndPostId(@Param("postId") Long postId);

    @Query("select c from ImageContent c where c.image.id = :imageId")
    ImageContent findByImageId(@Param("imageId") Long imageId);
}
