package com.kakao.together.external.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.service.cache.CacheService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Profile({"redis"})
public class RedisService implements CacheService {

    private final RedisHandler redisHandler;

    @Override
    public void setData(String key, String value, Integer expiredAt) {
        redisHandler.executeOperation(() -> redisHandler.getValueOperations().set(key, value, expiredAt, TimeUnit.SECONDS));
    }

    @Override
    public void setData(String key, Object value, Integer expiredAt) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = mapper.writeValueAsString(value);
        redisHandler.executeOperation(() -> redisHandler.getValueOperations().set(key, jsonValue, expiredAt, TimeUnit.SECONDS));
    }

    @Override
    public String getData(String key) {
        String value = (String) redisHandler.getValueOperations().get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value;
    }

    @Override
    public <T> T getData(String key, Class<T> clazz) throws JsonProcessingException {
        String value = (String) redisHandler.getValueOperations().get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(value, clazz);
    }

    @Override
    public void deleteData(String key) {
        redisHandler.executeOperation(() -> redisHandler.getRedisTemplate().delete(key));
    }
}
