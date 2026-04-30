package com.my.fomo.fomc.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fomc_minutes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FomcMinutes {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "fomc_minutes_name", length = 50)
    private String name;

    @Column(name = "fomc_interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "fomc_release_date")
    private LocalDate releaseDate;

    @Column(name = "fomc_start_time")
    private LocalDateTime startTime;

    @Column(name = "fomc_status")
    private boolean status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
