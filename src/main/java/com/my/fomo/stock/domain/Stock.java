package com.my.fomo.stock.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String name;

    @Column(name = "stock_name_kr", nullable = false, length = 100)
    private String nameKr;

    @Column(name = "stock_symbol", nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(name = "stock_cik", length = 20)
    private String cik;

    @Column(name = "stock_excd", nullable = false, length = 20)
    private String excd;

    @Column(name = "stock_rank", nullable = false)
    private int rank;

    @Column(name = "stock_logo_img", length = 255)
    private String logoImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;
}
