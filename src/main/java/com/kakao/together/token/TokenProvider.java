package com.kakao.together.token;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

public interface TokenProvider {
    TokenContainer generateTokenContainer(Authentication authentication);

    String removeBearerPrefix(@NotNull String token);

    Authentication getAuthentication(String token);
}
