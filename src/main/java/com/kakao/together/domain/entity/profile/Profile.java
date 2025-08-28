package com.kakao.together.domain.entity.profile;

import com.kakao.together.domain.entity.image.FileInfo;
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
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;
    @Column(nullable = false)
    private String nickname;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_info_id", nullable = false)
    private FileInfo profileImage;

    public void updateProfile(Profile profile) {
        this.nickname = profile.getNickname();
    }
}
