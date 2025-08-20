package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FundraisingRepository extends JpaRepository<Fundraising, Long> {

    Optional<Fundraising> findByIdAndFundraisingStatus(Long id, FundraisingStatus fundraisingStatus);

    @Query(value = "SELECT * FROM fundraising WHERE end_date BETWEEN :dateFrom AND :dateTo  ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    Optional<Fundraising> findFundraisingExpiringBetweenGivenDaysRandom(@Param("limit") int limit, @Param("dateFrom") String dateFrom, @Param("dateTo") String dateTo);

    @Query(value = """
    SELECT * FROM fundraising
    WHERE end_date <= CURRENT_DATE + INTERVAL 3 DAY
    ORDER BY RAND()
    LIMIT :limit
""", nativeQuery = true)
    List<Fundraising> findFundraisingsWithExpiringInThreeDaysLimit(@Param("limit") int limit);

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

    List<Fundraising> findByFundraisingStatus(FundraisingStatus fundraisingStatus);
}
