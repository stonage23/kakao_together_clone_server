package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.post.dto.ContentDto.ContentResponse;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingCurrent;
import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class FundraisingDto {

    // TODO 썸네일 설명 요약하는 필드 추가
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingResponse {
        private Long id;
        private String title;
        private String thumbnailUrl;
        private Integer targetAmount;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private AgencyDto agency;
        private Integer currentAmount;
        private Integer directDonationCount;
        private Integer indirectDonationCount;
        private Integer directDonationAmount;
        private Integer indirectDonationAmount;
        private String summary;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingRequest {

        public interface DRAFT {}
        public interface PUBLISHED {}

        private Long fundraisingId;
        @NotBlank(groups = {DRAFT.class, PUBLISHED.class}, message = "제목을 입력해주세요.")
        private String title;
        @NotNull(groups = {PUBLISHED.class}, message = "시작일을 지정해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @NotNull(groups = {PUBLISHED.class}, message = "종료일을 지정해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        @NotNull(groups = {PUBLISHED.class}, message = "목표 모금금액을 입력해주세요.")
        private Integer targetAmount;
        private Long thumbnailId;
        @NotBlank(groups = {PUBLISHED.class}, message = "내용을 입력해주세요.")
        private String html;
        @NotNull(groups = {PUBLISHED.class}, message = "주관 단체를 지정해주세요.")
        private Long agencyId;

        public Fundraising toEntity(Agency agency, @Nullable FileInfo thumbnail, @Nullable Post post) {
            return Fundraising.builder()
                    .title(this.title)
                    .startDate(this.startDate != null ? this.startDate.atTime(LocalTime.MIN) : null)
                    .endDate(this.startDate != null ? this.endDate.atTime(LocalTime.MAX) : null)
                    .agency(agency)
                    .post(post)
                    .thumbnail(thumbnail)
                    .id(this.fundraisingId)
                    .targetAmount(this.targetAmount)
                    .fundraisingCurrent(new FundraisingCurrent())
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class EditFundraisingResponse {

        private Long fundraisingId;
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer targetAmount;
        private Long thumbnailId;
        private String html;
        private Long agencyId;

        public static EditFundraisingResponse fromEntity(Fundraising fundraising, String content, Long thumbnailId) {
            return EditFundraisingResponse.builder()
                    .fundraisingId(fundraising.getId())
                    .title(fundraising.getTitle())
                    .startDate(fundraising.getStartDate())
                    .endDate(fundraising.getEndDate())
                    .targetAmount(fundraising.getTargetAmount())
                    .thumbnailId(thumbnailId)
                    .html(content)
                    .agencyId(fundraising.getAgency() != null ? fundraising.getAgency().getId() : null)
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SimpleDraftFundraisingResponse {
        private Long fundraisingId;
        private String title;
        private LocalDateTime updatedAt;

        public static SimpleDraftFundraisingResponse fromEntity(Fundraising fundraising) {
            return SimpleDraftFundraisingResponse.builder()
                    .fundraisingId(fundraising.getId())
                    .title(fundraising.getTitle())
                    .updatedAt(fundraising.getUpdatedAt())
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingStatusUpdateRequest {
        @NotBlank(message = "status 데이터를 넣어주세요.")
        private String status;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingPostEditResponse {
        private Long postId;
        private String postType;
        private String html;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class FundraisingPostResponse {
        private Long postId;
        private String postType;
        private List<ContentResponse> contents;
    }
}
