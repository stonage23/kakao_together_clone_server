package com.kakao.together.domain.entity.member;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.comment.Comment;
import com.kakao.together.domain.entity.image.FileInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Builder.Default
    @ColumnDefault("'MEMBER'")
    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;
    @Builder.Default
    @ColumnDefault("'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;
    private LocalDateTime deletedAt;

    @Embedded
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "writer", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String nickname, FileInfo updatedProfile, String birth, String address) {
        profile.update(nickname, updatedProfile, birth, address);
    }

    public void updateMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Member)) {
            return false;
        }

        Member member = (Member) o;

        return this.id != null && this.id.equals(member.getId());
    }
}
