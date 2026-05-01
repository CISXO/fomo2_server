package com.my.fomo.earnings.infrastructure;

import com.my.fomo.earnings.domain.StockFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockFinanceJpaRepository extends JpaRepository<StockFinance, String> {

    @Query("SELECT sf FROM StockFinance sf JOIN FETCH sf.stock s WHERE s.symbol = :symbol ORDER BY sf.releaseDate DESC")
    List<StockFinance> findByStockSymbol(@Param("symbol") String symbol);
}
