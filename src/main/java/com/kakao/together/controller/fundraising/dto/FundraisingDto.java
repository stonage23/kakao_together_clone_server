package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.controller.dto.ContentDto.ContentResponse;
import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingCurrent;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FundraisingDto {

    // TODO 썸네일 설명 요약하는 필드 추가
    @Builder
    @AllArgsConstructor
    @Getter
    public static class FundraisingResponse {
        private Long id;
        private String title;
        private ImageCommand thumbnailUrl;
        private Integer targetAmount;
        private LocalDate startDate;
        private LocalDate endDate;
        private FundraisingCurrent fundraisingStatus;
        private AgencyDto agency;
        private Integer currentAmount;
        private Integer directDonatorCount;
        private Integer indirectDonatorCount;
        private Integer directDonationAmount;
        private Integer indirectDonationAmount;


        public static FundraisingResponse fromEntity(Fundraising fundraising) {
            return FundraisingResponse.builder()
                    .id(fundraising.getId())
                    .title(fundraising.getTitle())
                    .thumbnailUrl(ImageCommand.fromEntity(fundraising.getThumbnail()))
                    .targetAmount(fundraising.getTargetAmount())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .fundraisingStatus(fundraising.getFundraisingCurrent())
                    .agency(AgencyDto.fromEntity(fundraising.getAgency()))
                    .currentAmount(fundraising.getFundraisingCurrent().getCurrentAmount())
                    .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                    .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                    .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                    .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingRequest {

        public interface Save {}
        public interface Update {}

        @NotBlank(groups = {Update.class})
        private Long fundraisingId;
        @NotBlank(groups = {Save.class, Update.class})
        private String title;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Integer TargetAmount;
        private String status;
        private ImageCommand thumbnail;
        @NotBlank(groups = {Save.class, Update.class})
        private String html;
        private Long agencyId;
        private Long postId;

        public Fundraising toEntity(Agency agency, @Nullable FileInfo thumbnail, @Nullable Post post) {
            return Fundraising.builder()
                    .title(this.title)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .agency(agency)
                    .post(post)
                    .thumbnail(thumbnail)
                    .id(this.fundraisingId)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingResponse {

        private Long fundraisingId;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer TargetAmount;
        private String status;
        private ImageCommand thumbnail;
        private String html;
        private Long agencyId;
        private Long postId;

        public static EditFundraisingResponse fromEntity(final Fundraising fundraising, final String buildedHtml) {
            return EditFundraisingResponse.builder()
                    .fundraisingId(fundraising.getId())
                    .title(fundraising.getTitle())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .thumbnail(fundraising.getThumbnail() != null ? ImageCommand.fromEntity(fundraising.getThumbnail()) : null)
                    .html(buildedHtml)
                    .postId(fundraising.getPost() != null ? fundraising.getPost().getId() : null)
                    .agencyId(fundraising.getAgency() != null ? fundraising.getAgency().getId() : null)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingSoonResponse {
        private Long id;
        private String thumbnailUrl;
        private String title;
        private Integer targetAmount;
        private Integer currentAmount;
        private String endDate;

        @Builder
        public FundraisingSoonResponse (Fundraising fundraising) {
            this.id = fundraising.getId();
            this.thumbnailUrl = fundraising.getThumbnail().getUrl();
            this.title = fundraising.getTitle();
            this.endDate = fundraising.getEndDate().toString();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingTopResponse {
        private Long id;
        private String thumbnailUrl;
        private String title;
        private Integer targetAmount;
        private Integer currentAmount;
        private String agency;

        @Builder
        public FundraisingTopResponse (Fundraising fundraising) {
            this.id = fundraising.getId();
            this.thumbnailUrl = fundraising.getThumbnail().getUrl();
            this.title = fundraising.getTitle();
            this.agency = fundraising.getAgency().getName();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SimpleEditFundraisingResponse {
        private String title;
        private LocalDateTime updatedAt;

        public static SimpleEditFundraisingResponse fromEntity(Fundraising fundraising) {
            return SimpleEditFundraisingResponse.builder()
                    .title(fundraising.getTitle())
                    .updatedAt(fundraising.getUpdatedAt())
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    public class FundraisingStatusUpdateRequest {
        private String status;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class FundraisingPostResponse {
        private Long postId;
        private String postType;
        private List<ContentResponse> contents;
    }
}
