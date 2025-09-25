package com.kakao.together.external.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.external.redis.exception.RedisServiceException;
import com.kakao.together.service.cache.CacheService;
import io.lettuce.core.RedisException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Profile({"redis"})
public class RedisService implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setData(String key, String value, Integer expiredAt) {
        execute(() -> redisTemplate.opsForValue().set(key, value, expiredAt, TimeUnit.SECONDS));
    }

    @Override
    public void setData(String key, Object value, Integer expiredAt) {

        ObjectMapper mapper = new ObjectMapper();

        String jsonValue;
        try {
            jsonValue = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RedisServiceException("object -> string 변환 실패", e);
        }

        execute(() -> redisTemplate.opsForValue().set(key, jsonValue, expiredAt, TimeUnit.SECONDS));
    }

    @Override
    public String getData(String key) {
        String value = (String) execute(() -> redisTemplate.opsForValue().get(key));

        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value;
    }

    @Override
    public <T> T getData(String key, Class<T> clazz) {
        String value = (String) execute(() -> redisTemplate.opsForValue().get(key));

        if (StringUtils.isBlank(value)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new RedisServiceException("json 파싱 예외 발생으로 redis 서버에서 조회한 데이터 반환 실패", e);
        }
    }

    @Override
    public void deleteData(String key) {
        execute(() -> redisTemplate.delete(key));
    }

    private void execute(Runnable operation) {
        try {
            operation.run();
        } catch (Exception e) {
            throw new RedisException("redis 작업 도중 문제발생", e);
        }
    }

    private <T> T execute(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (Exception e) {
            throw new RedisServiceException("redis 작업 도중 문제가 발생했습니다.", e);
        }
    }
}
