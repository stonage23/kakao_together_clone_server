package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.post.dto.ContentDto.ContentResponse;
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
        private String thumbnailUrl;
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
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingRequest {

        public interface DRAFT {}
        public interface PUBLISHED {}

        private Long fundraisingId;
        @NotBlank(groups = {DRAFT.class, PUBLISHED.class}, message = "제목을 입력해주세요.")
        private String title;
        @NotBlank(groups = {PUBLISHED.class}, message = "시작일을 지정해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @NotBlank(groups = {PUBLISHED.class}, message = "종료일을 지정해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        @NotBlank(groups = {PUBLISHED.class}, message = "목표 모금금액을 입력해주세요.")
        private Integer targetAmount;
        private Long thumbnailId;
        @NotBlank(groups = {PUBLISHED.class}, message = "내용을 입력해주세요.")
        private String html;
        @NotBlank(groups = {PUBLISHED.class}, message = "주관 단체를 지정해주세요.")
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
        private Integer targetAmount;
        private String status;
        private ImageCommand thumbnail;
        private List<ContentResponse> contents;
        private Long agencyId;
        private Long postId;

        public static EditFundraisingResponse fromEntity(Fundraising fundraising, List<ContentResponse> contents) {
            return EditFundraisingResponse.builder()
                    .fundraisingId(fundraising.getId())
                    .title(fundraising.getTitle())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .thumbnail(fundraising.getThumbnail() != null ? ImageCommand.fromEntity(fundraising.getThumbnail()) : null)
                    .contents(contents)
                    .postId(fundraising.getPost() != null ? fundraising.getPost().getId() : null)
                    .agencyId(fundraising.getAgency() != null ? fundraising.getAgency().getId() : null)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class SimpleDraftFundraisingResponse {
        private String title;
        private LocalDateTime updatedAt;

        public static SimpleDraftFundraisingResponse fromEntity(Fundraising fundraising) {
            return SimpleDraftFundraisingResponse.builder()
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
