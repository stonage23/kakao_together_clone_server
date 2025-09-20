package com.kakao.together.service.token;

import com.kakao.together.service.cache.impl.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenRepository implements RefreshTokenRepository<String> {

    private final RedisHandler redisHandler;

    private static final String BEARER = "Bearer ";
    private static final String REFRESH_TOKEN_FREFIX = "refresh_token ";
    private static final Integer EXPIRES_IN_MINUTES = 60*30;

    @Override
    public String findRefreshToken(String refreshToken) {
        String value = (String) redisHandler.getValueOperations().get(resolveKey(refreshToken));
        if (value == null) return "";
        else return value;
    }

    @Override
    public void saveRefreshToken(String refreshToken, String username) {
        redisHandler.getValueOperations().set(resolveKey(refreshToken), username, EXPIRES_IN_MINUTES);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisHandler.getRedisTemplate().delete(resolveKey(refreshToken));
    }

    private String resolveKey(String refreshToken) {
        return REFRESH_TOKEN_FREFIX + refreshToken;
    }
}
