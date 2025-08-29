package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FundraisingRepository extends JpaRepository<Fundraising, Long> {

    Optional<Fundraising> findByIdAndFundraisingStatus(Long id, FundraisingStatus fundraisingStatus);

    Optional<Fundraising> findByIdAndDraftStatus(Long id, DraftStatus draftStatus);

    @Query(value = """
    SELECT * FROM fundraising
    WHERE end_date <= CURRENT_DATE + INTERVAL :daysLeft DAY
    ORDER BY RAND()
    LIMIT :limit
""", nativeQuery = true)
    List<Fundraising> findFundraisingsWithExpiringInDaysLimit(@Param("limit") int limit, @Param("daysLeft") int daysLeft);

    @Query(value = """
    SELECT * FROM (
                SELECT * FROM fundraising
                ORDER BY current_amount / goal_amount DESC
                LIMIT 30
        ) AS top30
        ORDER BY RAND()
        LIMIT :limit;
""", nativeQuery = true)
    List<Fundraising> findFundraisingsWithTopLimit(@Param("limit") int limit);

    List<Fundraising> findByFundraisingStatus(DraftStatus draftStatus);
}
