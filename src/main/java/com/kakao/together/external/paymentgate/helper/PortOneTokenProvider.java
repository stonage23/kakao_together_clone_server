package com.kakao.together.external.paymentgate.helper;

import org.springframework.stereotype.Component;

@Component
public interface PortOneTokenProvider {
    default void afterPropertiesSet() {}

    String getToken();

    void setToken(String token, Long expiresIn);

    void setToken(String token);
}
