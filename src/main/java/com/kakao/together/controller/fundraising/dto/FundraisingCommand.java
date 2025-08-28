package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.agency.dto.AgencyCommand;
import com.kakao.together.controller.comment.dto.CommentCommand;
import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.controller.post.dto.PostCommand;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
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
    private DraftStatus draftStatus;
    private AgencyCommand agency;
    private ImageCommand thumbnail;
    private PostCommand post;
    @Builder.Default
    private List<CommentCommand> comments = new ArrayList<>();
    private Integer currentAmount;
    private Integer directDonorCount;
    private Integer indirectDonorCount;
    private Integer directDonationAmount;
    private Integer indirectDonationAmount;

    public Fundraising toEntity() {
        return Fundraising.builder()
                .id(this.id)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .targetAmount(this.targetAmount)
                .agency(this.agency.toEntity())
                .thumbnail(this.thumbnail.toEntity())
                .post(this.post.toEntity())
                .build();
    }
}
