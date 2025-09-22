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
    @Enumerated(EnumType.STRING)
    private FileStatus status;

    public void updateStatusToUsed() {
        if (!isValidConstraints()) throw new IllegalStateException("파일이 USED 상태가 되기 위해서는 모든 필드가 null이 아니어야 합니다. file_info_id=" + id);
        this.status = FileStatus.USED;
    }

    public void updateStatusToDeleted() {
        this.status = FileStatus.DELETED;
    }

    private boolean isValidConstraints() {
        return this.id != null && this.originalName != null && this.extension != null && this.contentType != null && this.size != null && this.status != null && this.savedName != null;
    }
}
