package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.dto.AgencyDto;
import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.document.Post;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class FundraisingDto {

    @AllArgsConstructor
    @Getter
    public static class FundraisingResponse {
        private Long id;
        private String title;
        private String thumbnailUrl;
        private Integer targetAmount;
        private Integer currentAmount;
        private String endDate;
        private Post post;
        private AgencyDto agency;

        @Builder
        public FundraisingResponse (Fundraising fundraising) {
            this.id = fundraising.getId();
            this.title = fundraising.getTitle();
            this.thumbnailUrl = fundraising.getThumbnail().getUrl();
            this.endDate = fundraising.getEndDate().toString();
            this.agency = AgencyDto.fromEntity(fundraising.getAgency());
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
        private Image thumbnail;
        @NotBlank(groups = {Save.class, Update.class})
        private String template;
        @NotBlank(groups = {Save.class, Update.class})
        private String html;
    }
}
