package com.lbs.speaking.speaking.service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public <T> T withLock(String key, Duration ttl, Supplier<T> supplier, Supplier<T> fallback) {
        String token = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, token, ttl);
        if (!Boolean.TRUE.equals(locked)) {
            return fallback.get();
        }
        try {
            return supplier.get();
        } finally {
            String current = redisTemplate.opsForValue().get(key);
            if (token.equals(current)) {
                redisTemplate.delete(key);
            }
        }
    }

    public boolean tryLock(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, UUID.randomUUID().toString(), ttl));
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
