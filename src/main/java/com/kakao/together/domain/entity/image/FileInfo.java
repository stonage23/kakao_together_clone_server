package com.kakao.together.domain.entity.image;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.file.FileStatus;
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
public class FileInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_info_id")
    private Long id;
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false, unique = true)
    private String savedName;
    @Column(nullable = false)
    private String extension;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private FileStatus status;

    public String generateFilename() {
        return this.savedName + "." + this.extension;
    }

    public void updateFileStatus(FileStatus status) {
        this.status = status;
    }
}
