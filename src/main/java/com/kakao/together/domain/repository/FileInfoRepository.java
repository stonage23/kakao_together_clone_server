package com.kakao.together.domain.repository;

import com.kakao.together.domain.entity.image.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findBySavedName(String savedName);
}
