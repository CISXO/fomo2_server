package com.my.fomo.stock.application;

import com.my.fomo.stock.application.dto.CompanyResponse;
import com.my.fomo.stock.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final StockRepository stockRepository;

    public List<CompanyResponse> getCompanies() {
        return stockRepository.findAllOrderByRank().stream()
                .map(CompanyResponse::from)
                .toList();
    }

    public List<CompanyResponse> searchCompanies(String keyword) {
        return stockRepository.searchByKeyword(keyword).stream()
                .map(CompanyResponse::from)
                .toList();
    }
}
