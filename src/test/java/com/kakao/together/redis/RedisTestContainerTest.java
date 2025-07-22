package com.kakao.together.redis;

import com.kakao.together.config.RedisTestContainersConfig;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {  RedisTestContainersConfig.class})
class RedisTestContainerTest {

    @Autowired
    RedisTestContainersConfig redisTestContainersConfig;

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    @PostConstruct
    public void init() {
        redisTemplate.setConnectionFactory(redisTestContainersConfig.redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
    }

    @AfterEach
    void afterEach() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    @DisplayName("redis 단일 데이터 저장 및 조회")
    void setSingleDataTest() {
        redisTemplate.opsForValue().set("key", "testValue");
        String value = (String) redisTemplate.opsForValue().get("key");

        assertThat(value).isEqualTo("testValue");
    }

    @Test
    @DisplayName("redis 단일 데이터 삭제")
    void getSingleDataTest() {
        redisTemplate.opsForValue().set("key", "testValue");
        assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("testValue");

        redisTemplate.delete("key");
        assertThat(redisTemplate.opsForValue().get("key")).isNull();
    }
}
