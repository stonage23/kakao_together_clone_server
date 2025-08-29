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
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * * FundraisingStatus을 수정하는 경우 내부 Fundraising 도메인 제약조건 체크 확인 필수 <br>
 * FundraisingCurrent 데이터 조작은 Fundraising과 독립적으로 이뤄진다.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Fundraising extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundraising_id")
    private Long id;

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer targetAmount;

    @Builder.Default
    @ColumnDefault("'DRAFT'")
    @Enumerated(EnumType.STRING)
    private DraftStatus draftStatus = DraftStatus.DRAFT;

    private FundraisingStatus fundraisingStatus;

    @OneToOne(fetch = FetchType.LAZY)
    private Agency agency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_info_id")
    private FileInfo thumbnail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    @OneToMany(mappedBy = "fundraising", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // TODO FundraisingCurrent 업데이트 하는 API 추가
    @Embedded
    private FundraisingCurrent fundraisingCurrent;

    public void updateFundraising(EditFundraisingRequest dto, Agency agency, FileInfo thumbnail) {
        this.title = dto.getTitle();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
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
            throw new CustomException(ErrorCode.NOT_NULL_VIOLATION, "서버 내부에 문제가 발생하였습니다. 서버 관리자에게 문의해주세요");
        }
    }
}

