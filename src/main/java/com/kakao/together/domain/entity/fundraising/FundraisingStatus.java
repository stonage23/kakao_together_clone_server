package com.kakao.together.domain.entity.fundraising;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FundraisingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundraising_status_id")
    private Long id;
    @Column(nullable = false)
    private Integer currentAmount;
    @Column(nullable = false)
    private Integer directDonatorCount;
    @Column(nullable = false)
    private Integer indirectDonatorCount;
    @Column(nullable = false)
    private Integer directDonationAmound;
    @Column(nullable = false)
    private Integer indirectDonationAmound;
    @Column(nullable = false)
    private Integer sharedCount;
}
