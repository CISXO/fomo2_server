package com.my.fomo.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private static final String REFRESH_KEY = "refresh:";
    private static final String BLACKLIST_KEY = "blacklist:";

    public String createRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                REFRESH_KEY + userId,
                refreshToken,
                refreshExpirationMs,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    public boolean isValidRefreshToken(String userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(REFRESH_KEY + userId);
        return refreshToken.equals(stored);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(REFRESH_KEY + userId);
    }

    public void blacklistAccessToken(String token, long remainingMs) {
        redisTemplate.opsForValue().set(
                BLACKLIST_KEY + token,
                "1",
                remainingMs,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + token));
    }
}
