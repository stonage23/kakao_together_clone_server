package com.kakao.together.controller;

import com.kakao.together.controller.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.dto.CommentDto.CommentUpdateRequest;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/")
    public ResponseEntity<Void> createComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CommentRequest requestDto) {
        commentService.createComment(userDetails.getId(), requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("id") Long id, @RequestBody CommentUpdateRequest requestDto) {
        commentService.updateComment(userDetails.getId(), id, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("id") Long id) {
        commentService.deleteComment(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }
}
