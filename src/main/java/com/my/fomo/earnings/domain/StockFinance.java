package com.my.fomo.earnings.domain;

import com.my.fomo.stock.domain.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_finances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockFinance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "fin_release_date")
    private LocalDate releaseDate;

    @Column(name = "fin_period_date")
    private LocalDate periodDate;

    @Column(name = "fin_eps_value", precision = 10, scale = 4)
    private BigDecimal epsValue;

    @Column(name = "fin_eps_forest", precision = 10, scale = 4)
    private BigDecimal epsForecast;

    @Column(name = "fin_revenue_value")
    private Long revenueValue;

    @Column(name = "fin_revenue_forest")
    private Long revenueForecast;

    @Column(name = "fin_info_name", length = 50)
    private String infoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "fin_hour")
    private FinHour finHour;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
