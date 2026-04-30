package com.my.fomo.stock.infrastructure;

import com.my.fomo.stock.domain.Stock;
import com.my.fomo.stock.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpaRepository;

    @Override
    public Optional<Stock> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Stock> findBySymbol(String symbol) {
        return jpaRepository.findBySymbol(symbol);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Stock> searchByKeyword(String keyword) {
        return jpaRepository.searchByKeyword(keyword);
    }
}
