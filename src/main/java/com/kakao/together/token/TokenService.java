package com.kakao.together.token;

import com.kakao.together.controller.token.TokenContainer;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;

import javax.crypto.SecretKey;
import java.util.Map;

public interface TokenService {
    SecretKey getSecretKey();

    TokenContainer generateTokenContainer(String subject, Map<String, Object> claims);

    String removeBearerPrefix(@NotNull String token);

    Claims extractAllClaims(String token);
}
