package com.my.fomo.fomc.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fomc_rate_decisions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FomcRateDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "fed_name", length = 50)
    private String name;

    @Column(name = "fed_start_time")
    private LocalDateTime startTime;

    @Column(name = "fed_release_date")
    private LocalDate releaseDate;

    @Column(name = "fed_actual_rate", precision = 5, scale = 2)
    private BigDecimal actualRate;

    @Column(name = "fed_forecast_rate", precision = 5, scale = 2)
    private BigDecimal forecastRate;

    @Column(name = "fed_previous_rate", precision = 5, scale = 2)
    private BigDecimal previousRate;

    @Column(name = "fed_status")
    private boolean status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
