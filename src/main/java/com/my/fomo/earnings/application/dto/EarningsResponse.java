package com.my.fomo.earnings.application.dto;

import com.my.fomo.earnings.domain.StockFinance;
import com.my.fomo.stock.domain.Stock;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public class EarningsResponse {

    private final StockInfo stock;
    private final List<FinanceInfo> finances;

    private EarningsResponse(StockInfo stock, List<FinanceInfo> finances) {
        this.stock = stock;
        this.finances = finances;
    }

    public static EarningsResponse from(Stock stock, List<StockFinance> finances) {
        StockInfo stockInfo = new StockInfo(
                stock.getId(),
                stock.getName(),
                stock.getNameKr(),
                stock.getSymbol(),
                stock.getExcd(),
                stock.getLogoImg()
        );
        List<FinanceInfo> financeInfos = finances.stream()
                .map(FinanceInfo::from)
                .toList();
        return new EarningsResponse(stockInfo, financeInfos);
    }

    @Getter
    public static class StockInfo {
        private final String id;
        private final String name;
        private final String nameKr;
        private final String symbol;
        private final String excd;
        private final String logo;

        public StockInfo(String id, String name, String nameKr, String symbol, String excd, String logo) {
            this.id = id;
            this.name = name;
            this.nameKr = nameKr;
            this.symbol = symbol;
            this.excd = excd;
            this.logo = logo;
        }
    }

    @Getter
    public static class FinanceInfo {
        private final String id;
        private final String finQuarter;
        private final LocalDate finReleaseDate;
        private final LocalDate finPeriodDate;
        private final BigDecimal finEpsValue;
        private final BigDecimal finEpsForecast;
        private final String finRevenueValue;
        private final String finRevenueForecast;
        private final String finHour;

        private FinanceInfo(String id, String finQuarter, LocalDate finReleaseDate, LocalDate finPeriodDate,
                            BigDecimal finEpsValue, BigDecimal finEpsForecast,
                            String finRevenueValue, String finRevenueForecast, String finHour) {
            this.id = id;
            this.finQuarter = finQuarter;
            this.finReleaseDate = finReleaseDate;
            this.finPeriodDate = finPeriodDate;
            this.finEpsValue = finEpsValue;
            this.finEpsForecast = finEpsForecast;
            this.finRevenueValue = finRevenueValue;
            this.finRevenueForecast = finRevenueForecast;
            this.finHour = finHour;
        }

        public static FinanceInfo from(StockFinance sf) {
            return new FinanceInfo(
                    sf.getId(),
                    parseQuarter(sf.getInfoName()),
                    sf.getReleaseDate(),
                    sf.getPeriodDate(),
                    sf.getEpsValue(),
                    sf.getEpsForecast(),
                    formatRevenue(sf.getRevenueValue()),
                    formatRevenue(sf.getRevenueForecast()),
                    sf.getFinHour() != null ? sf.getFinHour().name() : null
            );
        }

        private static String parseQuarter(String infoName) {
            if (infoName == null) return null;
            try {
                int quarterNum = Integer.parseInt(infoName);
                int year = 2020 + (quarterNum - 1) / 4;
                int quarter = ((quarterNum - 1) % 4) + 1;
                return year + "년 " + quarter + "분기";
            } catch (NumberFormatException e) {
                return infoName;
            }
        }

        private static String formatRevenue(Long value) {
            if (value == null) return null;
            if (value >= 1_000_000_000L) return String.format("%.2fB", value / 1_000_000_000.0);
            if (value >= 1_000_000L) return String.format("%.2fM", value / 1_000_000.0);
            return String.format("%,d", value);
        }
    }
}
