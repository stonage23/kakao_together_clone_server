package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.agency.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
}
