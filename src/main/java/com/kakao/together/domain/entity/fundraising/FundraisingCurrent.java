package com.kakao.together.domain.entity.fundraising;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class FundraisingCurrent {

    @Builder.Default
    @ColumnDefault("0")
    private Integer currentAmount = 0;
    @Builder.Default
    @ColumnDefault("0")
    private Integer directDonorCount = 0;
    @Builder.Default
    @ColumnDefault("0")
    private Integer indirectDonorCount = 0;
    @Builder.Default
    @ColumnDefault("0")
    private Integer directDonationAmount = 0;
    @Builder.Default
    @ColumnDefault("0")
    private Integer indirectDonationAmount = 0;
}
