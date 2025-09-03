package com.kakao.together.service.cache.impl;

import com.kakao.together.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//@Profile({"redis"})
@Slf4j
public class RedisHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    public RedisTemplate<String, Object> getRedisTemplate() { return this.redisTemplate; }

    public void executeOperation(Runnable operation) {
        try {
            operation.run();
        } catch (Exception e) {
            throw new CustomException(e, "redis 캐싱 관련 작업 중 예외 발생");
        }
    }
}
