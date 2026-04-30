package com.my.fomo.fomc.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fomc_minutes_script")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FomcMinutesScript {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fomc_minutes_id", nullable = false)
    private FomcMinutes fomcMinutes;

    @Column(name = "minutes_release_content_en", length = 255)
    private String contentEn;

    @Column(name = "minutes_release_content_kr", length = 255)
    private String contentKr;

    @Column(name = "minutes_release_content_an", length = 255)
    private String contentAn;

    @Column(name = "minutes_release_content_en_pdf", length = 255)
    private String contentEnPdf;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
