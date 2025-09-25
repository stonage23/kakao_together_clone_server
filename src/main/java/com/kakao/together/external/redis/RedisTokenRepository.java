package com.kakao.together.external.redis;

import com.kakao.together.external.redis.exception.RedisServiceException;
import com.kakao.together.service.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenRepository implements RefreshTokenRepository {

    private final RedisService redisService;

    private static final String REFRESH_TOKEN_FREFIX = "refresh_token ";
    @Value("${business.constants.token.refresh-token.expiring}")
    private Integer EXPIRES_IN_SECONDS;

    @Override
    public String findRefreshToken(String refreshToken) {
        return redisService.getData(resolveKey(refreshToken));
    }

    @Override
    public void saveRefreshToken(String refreshToken, String username) throws RedisServiceException {
        redisService.setData(resolveKey(refreshToken), username, EXPIRES_IN_SECONDS);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisService.deleteData(resolveKey(refreshToken));
    }

    private String resolveKey(String refreshToken) {
        return REFRESH_TOKEN_FREFIX + refreshToken;
    }
}
