package com.my.fomo.chart.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fomo.chart.infrastructure.KisTokenClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisTokenService {

    private final StringRedisTemplate redisTemplate;
    private final KisTokenClient tokenClient;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_KEY = "kis:period_token";

    public String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(TOKEN_KEY);
        if (cached != null) {
            try {
                Map<String, Object> tokenMap = objectMapper.readValue(cached, new TypeReference<>() {});
                String accessToken = (String) tokenMap.get("access_token");
                if (accessToken != null) return accessToken;
            } catch (Exception e) {
                log.warn("캐시된 KIS 토큰 파싱 실패, 재발급", e);
            }
        }
        return refreshAndGetToken();
    }

    public String refreshAndGetToken() {
        Map<String, Object> token = tokenClient.fetchAccessToken();
        saveToRedis(token);
        return (String) token.get("access_token");
    }

    private void saveToRedis(Map<String, Object> token) {
        try {
            String json = objectMapper.writeValueAsString(token);
            // KIS 토큰 유효기간 24시간 → 23시간으로 저장 (만료 전 갱신)
            redisTemplate.opsForValue().set(TOKEN_KEY, json, 23, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("KIS 토큰 Redis 저장 실패", e);
        }
    }
}
