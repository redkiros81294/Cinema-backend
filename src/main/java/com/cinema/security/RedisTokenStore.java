package com.cinema.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenStore {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void storeToken(String userId, String token, long expirationInSeconds) {
        String key = TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token, expirationInSeconds, TimeUnit.SECONDS);
    }

    public void storeRefreshToken(String userId, String refreshToken, long expirationInSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationInSeconds, TimeUnit.SECONDS);
    }

    public String getToken(String userId) {
        String key = TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public String getRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void blacklistToken(String token, long expirationInSeconds) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationInSeconds, TimeUnit.SECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void removeToken(String userId) {
        String tokenKey = TOKEN_PREFIX + userId;
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(refreshTokenKey);
    }
} 