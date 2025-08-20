package com.kakao.together.api.paymentgate.cache;

import org.springframework.stereotype.Component;

@Component
public interface PortOneTokenProvider {
    default void afterPropertiesSet() {}

    String getToken();

    void setToken(String token, Long expiresIn);

    void setToken(String token);
}
