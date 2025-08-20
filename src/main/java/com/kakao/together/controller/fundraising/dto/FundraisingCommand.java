package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.domain.entity.image.Image;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.fundraising.FundraisingCurrent;
import com.kakao.together.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class FundraisingCommand {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetAmount;
    private FundraisingStatus fundraisingStatus;
    private Agency agency;
    private Image thumbnail;
    private Post post;
    private List<Comment> comments = new ArrayList<>();
    private FundraisingCurrent fundraisingCurrent;

}
