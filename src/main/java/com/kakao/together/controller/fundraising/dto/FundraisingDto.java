package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.dto.AgencyDto;
import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.fundraising.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
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
        private ImageDto thumbnailUrl;
        private Integer targetAmount;
        private LocalDate startDate;
        private LocalDate endDate;
        private Status status;
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
                    .thumbnailUrl(ImageDto.fromEntity(fundraising.getThumbnail()))
                    .targetAmount(fundraising.getTargetAmount())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .status(fundraising.getStatus())
                    .agency(AgencyDto.fromEntity(fundraising.getAgency()))
                    .currentAmount(fundraising.getFundraisingStatus().getCurrentAmount())
                    .directDonationAmount(fundraising.getFundraisingStatus().getDirectDonationAmount())
                    .indirectDonationAmount(fundraising.getFundraisingStatus().getIndirectDonationAmount())
                    .directDonationAmount(fundraising.getFundraisingStatus().getDirectDonationAmount())
                    .indirectDonationAmount(fundraising.getFundraisingStatus().getIndirectDonationAmount())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingDto {

        public interface UpdateDraft {}
        public interface Save {}
        public interface Update {}

        @NotBlank(groups = {UpdateDraft.class, Update.class})
        private Long fundraisingId;
        @NotBlank(groups = {Save.class, Update.class})
        private String title;
        @NotBlank(groups = {Save.class, Update.class})
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @NotBlank(groups = {Save.class, Update.class})
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        @NotBlank(groups = {Save.class, Update.class})
        private ImageDto thumbnail;
        @NotBlank(groups = {Save.class, Update.class})
        private String html;
        @NotBlank(groups = {Save.class, Update.class})
        private Long agencyId;
        @NotBlank(groups = {Save.class, Update.class})
        private Long postId;

        public Fundraising toEntity(@Nullable Agency agency, @Nullable Image thumbnail, @Nullable Post post) {
            Fundraising.FundraisingBuilder builder = Fundraising.builder()
                    .title(this.title)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .status(Status.TEMPORARY)
                    .agency(agency)
                    .post(post)
                    .thumbnail(thumbnail);
            if (fundraisingId != null)
                        builder.id(this.fundraisingId);

            return builder.build();
        }

        public static EditFundraisingDto fromEntity(final Fundraising fundraising, final String buildedHtml) {
            return EditFundraisingDto.builder()
                    .fundraisingId(fundraising.getId())
                    .title(fundraising.getTitle())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .thumbnail(ImageDto.fromEntity(fundraising.getThumbnail()))
                    .html(buildedHtml)
                    .postId(fundraising.getPost().getId())
                    .agencyId(fundraising.getAgency().getId())
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingSingleResponse {
        private Long id;
        private String thumbnailUrl;
        private String title;
        private Integer targetAmount;
        private Integer currentAmount;
        private String summary;

        @Builder
        public FundraisingSingleResponse (Fundraising fundraising) {
            this.id = fundraising.getId();
            this.thumbnailUrl = fundraising.getThumbnail().getUrl();
            this.title = fundraising.getTitle();
        }

        public String generateSummary (Post post, int length) {
            StringBuilder summary = new StringBuilder();
            List<Content> contents = post.getContents();
            int currentLength = 0;

            for (Content content : contents) {
                if (content instanceof SubTitleContent) {
                    String subtitle = ((SubTitleContent) content).getSubtitle();
                    currentLength += subtitle.length();
                    if (currentLength <= length) {
                        summary.append(subtitle).append(" ");
                    } else {
                        int remainingLength = length - (currentLength - subtitle.length());
                        summary.append(subtitle, 0, remainingLength).append(" ");
                        break;
                    }
                } else if (content instanceof TextContent) {
                    String text = ((TextContent) content).getText();
                    currentLength += text.length();
                    if (currentLength <= length) {
                        summary.append(text).append(" ");
                    } else {
                        int remainingLength = length - (currentLength - text.length());
                        summary.append(text, 0, remainingLength).append(" ");
                        break;
                    }
                }
                if (currentLength >= length) {
                    break;
                }
            }
            return summary.toString();
        }
    }

    @Builder
    @AllArgsConstructor
    public static class SimpleEditFundraisingResponse {
        String title;
        LocalDateTime updatedAt;

        public static SimpleEditFundraisingResponse fromEntity(Fundraising fundraising) {
            return SimpleEditFundraisingResponse.builder()
                    .title(fundraising.getTitle())
                    .updatedAt(fundraising.getUpdatedAt())
                    .build();
        }
    }
}
