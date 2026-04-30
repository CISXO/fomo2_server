package com.my.fomo.stock.infrastructure;

import com.my.fomo.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, String> {

    Optional<Stock> findBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE s.name LIKE %:keyword% OR s.nameKr LIKE %:keyword% OR s.symbol LIKE %:keyword%")
    List<Stock> searchByKeyword(@Param("keyword") String keyword);
}
