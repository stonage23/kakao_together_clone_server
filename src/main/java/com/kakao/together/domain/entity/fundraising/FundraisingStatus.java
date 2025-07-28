package com.kakao.together.domain.entity.fundraising;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class FundraisingStatus {

//    @Column(nullable = false)
    private Integer currentAmount;
//    @Column(nullable = false)
    private Integer directDonatorCount;
//    @Column(nullable = false)
    private Integer indirectDonatorCount;
//    @Column(nullable = false)
    private Integer directDonationAmount;
//    @Column(nullable = false)
    private Integer indirectDonationAmount;
}
