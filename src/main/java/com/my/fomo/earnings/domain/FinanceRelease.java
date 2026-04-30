package com.my.fomo.earnings.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "finance_releases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinanceRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_finances_id", nullable = false)
    private StockFinance stockFinance;

    @Column(name = "stock_release_content_en", length = 255)
    private String contentEn;

    @Column(name = "stock_release_content_kr", length = 255)
    private String contentKr;

    @Column(name = "stock_release_content_an", length = 255)
    private String contentAn;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
