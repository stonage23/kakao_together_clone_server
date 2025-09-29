package com.kakao.together.domain.entity.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.agency.Agency;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * * FundraisingStatus을 수정하는 경우 내부 Fundraising 도메인 제약조건 체크 확인 필수 <br>
 * FundraisingCurrent 데이터 조작은 Fundraising과 독립적으로 이뤄진다.
 */
@Entity
@Table(indexes = @Index(name = "idx_fundraising_status", columnList = "fundraising_status"))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Fundraising extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundraising_id")
    private Long id;

    @NotBlank
    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer targetAmount;

    @Builder.Default
    @ColumnDefault("'DRAFT'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftStatus draftStatus = DraftStatus.DRAFT;

    @Builder.Default
    @ColumnDefault("'PAUSE'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundraisingStatus fundraisingStatus = FundraisingStatus.PAUSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", unique = false)
    private Agency agency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_info_id")
    private FileInfo thumbnail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    @OneToMany(mappedBy = "fundraising", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // TODO FundraisingCurrent 업데이트 하는 API 추가
    @Embedded
    @Builder.Default
    private FundraisingCurrent fundraisingCurrent = new FundraisingCurrent();

    public void updateFundraising(EditFundraisingRequest dto, Agency agency, FileInfo thumbnail) {
        this.title = dto.getTitle();
        this.startDate = dto.getStartDate().atTime(LocalTime.MIN);
        this.endDate = dto.getEndDate().atTime(LocalTime.MAX);
        this.targetAmount = dto.getTargetAmount();
        this.agency = agency;
        this.thumbnail = thumbnail;
    }

    public void updateDraftToCreated() {
        validateConstraints();
        this.draftStatus = DraftStatus.PUBLISHED;
    }

    public void updateFundraisingStatus(String status) {
        FundraisingStatus fundraisingStatus = switch (status) {
            case "ONGOING" -> FundraisingStatus.ONGOING;
            case "PAUSE" -> FundraisingStatus.PAUSE;
            case "ENDED" -> FundraisingStatus.ENDED;
            default -> throw new CustomException(ErrorCode.INVALID_ARGUMENT, "적절하지 않은 FundraisingStatus: " + status);
        };

        validateConstraints();

        this.fundraisingStatus = fundraisingStatus;
    }

    // TODO ErrorCode 디버깅용 메시지도 생성자에 넣어야할듯..
    private void validateConstraints() {
        if (this.title == null || this.startDate == null || this.endDate == null || this.targetAmount == null || agency == null || this.thumbnail == null || this.post == null) {
            throw new CustomException(ErrorCode.NOT_NULL_VIOLATION, "모금을 발행하기 위해서는 모든 입력란을 채워야 합니다.");
        }
    }

    public boolean isOngoing() {
        return this.fundraisingStatus == FundraisingStatus.ONGOING;
    }
}

