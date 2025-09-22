package com.kakao.together.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CacheService {
    void setData(String key, String value, Integer expiredAt);

    void setData(String key, Object value, Integer expiredAt) throws JsonProcessingException;

    String getData(String key);

    <T> T getData(String key, Class<T> clazz) throws JsonProcessingException;

    void deleteData(String key);
}
