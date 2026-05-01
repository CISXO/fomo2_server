package com.my.fomo.earnings.infrastructure;

import com.my.fomo.earnings.domain.StockFinance;
import com.my.fomo.earnings.domain.StockFinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockFinanceRepositoryImpl implements StockFinanceRepository {

    private final StockFinanceJpaRepository jpaRepository;

    @Override
    public List<StockFinance> findByStockSymbol(String symbol) {
        return jpaRepository.findByStockSymbol(symbol);
    }
}
