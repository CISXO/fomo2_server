package com.my.fomo.fomc.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fomc_decision_statements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FomcDecisionStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fomc_rate_decisions_id", nullable = false)
    private FomcRateDecision rateDecision;

    @Column(name = "decisions_release_content_an", length = 255)
    private String contentAn;

    @Column(name = "decisions_release_content_en_html_impl", length = 255)
    private String contentEnHtmlImpl;

    @Column(name = "decisions_release_content_en_html_stat", length = 255)
    private String contentEnHtmlStat;

    @Column(name = "decisions_release_content_en_pdf_file", length = 255)
    private String contentEnPdf;

    @Column(name = "decisions_release_content_kr_html_impl", length = 255)
    private String contentKrHtmlImpl;

    @Column(name = "decisions_release_content_kr_html_stat", length = 255)
    private String contentKrHtmlStat;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
