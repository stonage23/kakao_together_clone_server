package com.kakao.together.paymentgate.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisTokenProvider implements PortOneTokenProvider, InitializingBean {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenProvider(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final Logger log = LoggerFactory.getLogger(RedisTokenProvider.class);
    private static final String ACCESS_TOKEN = "portone_access_token";
    private static final Long DEFAULT_TOKEN_EXPIRE = 60 * 30L;

    @Override
    public void afterPropertiesSet() {
        checkSerializerConfiguration();
    }

    private void checkSerializerConfiguration() {
        RedisSerializer<?> keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer<?> valueSerializer = redisTemplate.getValueSerializer();

        boolean isKeySafe = isSafeSerializer(keySerializer);
        boolean isValueSafe = isSafeSerializer(valueSerializer);

        if (!isKeySafe || !isValueSafe) {
            log.warn("[WARNING] RedisTemplate 직렬화 설정이 안전하지 않습니다; StringRedisSerializer 직렬화를 설정하세요; 예시: redisTemplate.setKeySerializer(new StringRedisSerializer());");
        }
    }

    private boolean isSafeSerializer(RedisSerializer<?> serializer) {
        return (serializer instanceof StringRedisSerializer);
    }

    @Override
    public String getToken() {
        return redisTemplate.opsForValue().get(ACCESS_TOKEN);
    }

    @Override
    public void setToken(String token, Long expiresInSeconds) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN, token, Duration.ofSeconds(expiresInSeconds));
    }

    public void setToken(String token) {
        this.setToken(token, DEFAULT_TOKEN_EXPIRE);
    }
}
