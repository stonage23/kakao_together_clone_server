package com.kakao.together.domain.entity.member;

import com.kakao.together.file.domain.FileInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Profile {

    @Column(nullable = false)
    private String nickname;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_info_id")
    private FileInfo profileImage;
    private String birth;
    private String address;

    protected void update(String nickname, FileInfo profileImage, String birth, String address) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.birth = birth;
        this.address = address;
    }
}
