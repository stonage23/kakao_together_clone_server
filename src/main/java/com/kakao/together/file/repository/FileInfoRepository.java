package com.kakao.together.file.repository;

import com.kakao.together.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findBySavedName(String savedName);
}
