package com.kakao.together.domain.entity.fundraising;

import com.kakao.together.domain.entity.BaseTimeEntity;
import com.kakao.together.domain.entity.Image;
import com.kakao.together.domain.entity.document.Document;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table (name = "Fundraising")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Fundraising extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundraising_id")
    private Long id;

    @Column (nullable = false)
    private String title;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(length = 12)
    private String  status = "CREATED";

    @OneToOne(fetch = FetchType.LAZY)
    private Agency agency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image thumbnail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_status_id")
    private FundraisingStatus fundraisingStatus;
}