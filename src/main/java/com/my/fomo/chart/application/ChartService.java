package com.my.fomo.chart.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fomo.chart.application.dto.DailyChartRequest;
import com.my.fomo.chart.application.dto.MinutesChartRequest;
import com.my.fomo.chart.infrastructure.KisChartClient;
import com.my.fomo.chart.infrastructure.KisRequestQueue;
import com.my.fomo.stock.domain.Stock;
import com.my.fomo.stock.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {

    private final KisChartClient chartClient;
    private final KisRequestQueue requestQueue;
    private final StringRedisTemplate redisTemplate;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "chart:";

    public Map<String, Object> getMinutesChart(MinutesChartRequest req) {
        resolveExcd(req);
        req.setSymb(normalizeSymbol(req.getSymb()));

        boolean isHistorical = StringUtils.hasText(req.getKeyb());
        String cacheKey = buildCacheKey("minutes", req.getExcd(), req.getSymb(),
                req.getNmin(), req.getPinc(), req.getKeyb());

        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) return cached;

        Map<String, Object> data = requestQueue.submit(() -> chartClient.fetchMinutesChart(req));

        if (isSuccess(data)) {
            long ttlSeconds = isHistorical ? TimeUnit.DAYS.toSeconds(1) : 5;
            saveToCache(cacheKey, data, ttlSeconds);
        }

        return data;
    }

    public Map<String, Object> getDailyChart(DailyChartRequest req) {
        resolveExcd(req);
        req.setSymb(normalizeSymbol(req.getSymb()));

        boolean isHistorical = StringUtils.hasText(req.getBymd());
        String cacheKey = buildCacheKey("daily", req.getExcd(), req.getSymb(),
                req.getGubn(), req.getBymd(), req.getModp());

        if (isHistorical) {
            Map<String, Object> cached = getFromCache(cacheKey);
            if (cached != null) return cached;
        }

        Map<String, Object> data = requestQueue.submit(() -> chartClient.fetchDailyChart(req));

        if (isSuccess(data) && isHistorical) {
            saveToCache(cacheKey, data, TimeUnit.DAYS.toSeconds(1));
        }

        return data;
    }

    private void resolveExcd(MinutesChartRequest req) {
        if (StringUtils.hasText(req.getExcd())) return;
        req.setExcd(lookupExcd(req.getSymb()));
    }

    private void resolveExcd(DailyChartRequest req) {
        if (StringUtils.hasText(req.getExcd())) return;
        req.setExcd(lookupExcd(req.getSymb()));
    }

    private String lookupExcd(String symbol) {
        if ("QQQ".equals(symbol)) return "NAS";
        if ("SPY".equals(symbol)) return "AMS";
        return stockRepository.findBySymbol(symbol)
                .map(Stock::getExcd)
                .orElse("AMS");
    }

    private String normalizeSymbol(String symbol) {
        if ("BRK-B".equals(symbol)) return "BRK/B";
        if ("BF-B".equals(symbol)) return "BF/B";
        return symbol;
    }

    private String buildCacheKey(String type, String... parts) {
        String joined = Arrays.stream(parts)
                .map(p -> p == null ? "" : p)
                .collect(Collectors.joining("|"));
        return CACHE_PREFIX + type + ":" + joined;
    }

    private boolean isSuccess(Map<String, Object> data) {
        if (data == null) return false;
        Object rtCd = data.get("rt_cd");
        return "0".equals(rtCd) || Integer.valueOf(0).equals(rtCd);
    }

    private Map<String, Object> getFromCache(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("차트 캐시 파싱 실패: {}", key);
            redisTemplate.delete(key);
            return null;
        }
    }

    private void saveToCache(String key, Map<String, Object> data, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("차트 캐시 저장 실패: {}", key);
        }
    }
}
