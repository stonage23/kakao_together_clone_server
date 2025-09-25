package com.kakao.together.token;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface TokenService {

//    TokenContainer generateTokenContainer(String subject, List<String> authorities);

//    String removeBearerPrefix(@NotNull String token);

    String buildToken(Map<String, Object> claims, String subject, Long expirationPeriod);

    Claims extractAllClaims(String token);

//    List<String> getAuthorities(Claims claims);
}
