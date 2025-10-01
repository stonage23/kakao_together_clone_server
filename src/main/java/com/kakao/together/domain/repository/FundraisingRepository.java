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
                WHERE end_date <= NOW() + INTERVAL :daysLeft DAY
                AND NOW() < end_date
                AND fundraising_status = 'ONGOING'
                ORDER BY RAND()
                LIMIT :limit
            """, nativeQuery = true)
    List<Fundraising> findFundraisingsWithExpiringInDaysLimit(@Param("limit") int limit, @Param("daysLeft") int daysLeft);

    @Query(value = """
                    SELECT f.*,
                   d.donation_count
            FROM fundraising AS f
                     INNER JOIN (
                        SELECT fundraising_id,
                               donation_count
                        FROM (SELECT d.fundraising_id,
                                     COUNT(d.donation_id) AS donation_count
                              FROM donation AS d
                                       INNER JOIN
                                   fundraising AS f ON d.fundraising_id = f.fundraising_id
                              WHERE DATE(d.updated_at) = CURDATE() - INTERVAL 1 DAY
                                AND f.fundraising_status = 'ONGOING'
                                AND d.status = 'COMPLETE'
                              GROUP BY d.fundraising_id
                              ORDER BY donation_count DESC
                              LIMIT 30) AS top_30
                        ORDER BY RAND()
                        LIMIT 3
                    ) AS d ON f.fundraising_id = d.fundraising_id;
            """, nativeQuery = true)
    List<Fundraising> findFundraisingsWithTopLimit(@Param("limit") int limit);

    List<Fundraising> findByDraftStatus(DraftStatus draftStatus);

    @Query(value = """
            SELECT * from (
                      SELECT * from fundraising
                      WHERE fundraising_status = 'ONGOING'
                        AND end_date >= NOW()
                      ORDER BY ROUND((current_amount / target_amount) * 100, 2) DESC
                      LIMIT 30
                  ) F
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<Fundraising> findFundraisingsWithNearingGoal(int limit);

    @Query(value = """
            SELECT *
            FROM fundraising
            WHERE fundraising_status = 'ONGOING'
            AND end_date >= NOW()
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<Fundraising> findFundraisingsWithOngoingAndNotExpired(@Param("limit") int limit);
 }
