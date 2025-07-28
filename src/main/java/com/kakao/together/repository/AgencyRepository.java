package com.kakao.together.repository;

import com.kakao.together.domain.entity.fundraising.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
}
