package com.kakao.together.domain.entity.agency;

import com.kakao.together.domain.entity.image.Image;
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
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private Long id;
    @Column(nullable = false)
    private String name;
    private String parentCompanyName;
    @Column(nullable = false)
    private String buisinessNumber;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String organizationType;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image logo;
}
