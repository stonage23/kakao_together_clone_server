package com.kakao.together.token;

import com.kakao.together.controller.dto.TokenContainer;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;

import javax.crypto.SecretKey;
import java.util.Map;

public interface TokenService {
    SecretKey getSecretKey();

    String createBearerAccessToken(Map<String, Object> claims);

    String createBearerRefreshToken(Map<String, Object> claims);

    TokenContainer generateTokenContainerWithCommonClaims(Map<String, Object> claims);

    String withBearerPrefix(String token);

    String removeBearerPrefix(@NotNull String token);

    Claims extractAllClaims(String token);
}
