package com.my.fomo.earnings.application;

import com.my.fomo.earnings.application.dto.EarningsResponse;
import com.my.fomo.earnings.domain.StockFinance;
import com.my.fomo.earnings.domain.StockFinanceRepository;
import com.my.fomo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EarningsService {

    private final StockFinanceRepository stockFinanceRepository;

    @Transactional(readOnly = true)
    public EarningsResponse getEarningsBySymbol(String symbol) {
        List<StockFinance> finances = stockFinanceRepository.findByStockSymbol(symbol.toUpperCase());
        if (finances.isEmpty()) {
            throw new BusinessException("종목을 찾을 수 없습니다: " + symbol, HttpStatus.NOT_FOUND);
        }
        return EarningsResponse.from(finances.get(0).getStock(), finances);
    }
}
