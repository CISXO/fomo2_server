package com.my.fomo.fomc.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fomc_speeches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FomcSpeech {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fomc_rate_decisions_id", nullable = false)
    private FomcRateDecision rateDecision;

    @Column(name = "fomc_speech_content_en", length = 255)
    private String contentEn;

    @Column(name = "fomc_speech_content_kr", length = 255)
    private String contentKr;

    @Column(name = "fomc_speech_content_an", length = 255)
    private String contentAn;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
