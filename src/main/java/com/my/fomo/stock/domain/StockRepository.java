package com.my.fomo.stock.domain;

import java.util.List;
import java.util.Optional;

public interface StockRepository {

    Optional<Stock> findById(String id);

    Optional<Stock> findBySymbol(String symbol);

    List<Stock> findAllOrderByRank();

    List<Stock> searchByKeyword(String keyword);
}
