package com.my.fomo.stock.application.dto;

import com.my.fomo.stock.domain.Stock;
import lombok.Getter;

@Getter
public class CompanyResponse {

    private final String id;
    private final String name;
    private final String nameKr;
    private final String symbol;
    private final String logo;
    private final int rank;
    private final String sector;
    private final String industry;

    private CompanyResponse(String id, String name, String nameKr, String symbol,
                            String logo, int rank, String sector, String industry) {
        this.id = id;
        this.name = name;
        this.nameKr = nameKr;
        this.symbol = symbol;
        this.logo = logo;
        this.rank = rank;
        this.sector = sector;
        this.industry = industry;
    }

    public static CompanyResponse from(Stock stock) {
        return new CompanyResponse(
                stock.getId(),
                stock.getName(),
                stock.getNameKr(),
                stock.getSymbol(),
                stock.getLogoImg(),
                stock.getRank(),
                stock.getSector().getName(),
                stock.getIndustry().getName()
        );
    }
}
