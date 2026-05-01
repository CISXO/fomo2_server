package com.my.fomo.earnings.domain;

import java.util.List;

public interface StockFinanceRepository {
    List<StockFinance> findByStockSymbol(String symbol);
}
