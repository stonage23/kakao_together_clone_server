package com.kakao.together.api.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Profile({"redis"})
public class RedisService {

    private final RedisHandler redisHandler;

    public void setSingleData(String key, Object value) {
        redisHandler.executeOperation(() -> redisHandler.getValueOperations().set(key, value));
    }

    public void setSingleData(String key, Object value, Duration duration) {
        redisHandler.executeOperation(() -> redisHandler.getValueOperations().set(key, value, duration));
    }

    public Object getSingleData(String key) {
        if (redisHandler.getValueOperations().get(key) == null) return "";
        return String.valueOf(redisHandler.getValueOperations().get(key));
    }

    public void deleteSingleData(String key) {
        redisHandler.executeOperation(() -> redisHandler.getRedisTemplate().delete(key));
    }
}
