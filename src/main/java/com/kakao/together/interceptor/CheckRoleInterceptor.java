package com.kakao.together.interceptor;

import com.kakao.together.annotation.Admin;
import com.kakao.together.annotation.Member;
import com.kakao.together.domain.entity.member.Role;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CheckRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {

        if (!(handler instanceof HandlerMethod)) return true;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (this.checkAnnotation(handler, Admin.class)) {
            if (authentication == null)
                throw new CustomException(ErrorCode.UNAUTHENTICATED_REQUEST);
            validateAuthority(authentication, Role.ADMIN);
        }

        if (this.checkAnnotation(handler, Member.class)) {
            if (authentication == null)
                throw new CustomException(ErrorCode.UNAUTHENTICATED_REQUEST);
            validateAuthority(authentication, Role.MEMBER);
        }

        return true;
    }

    private void validateAuthority(Authentication authentication, Role role) {
        boolean hasRole = authentication.getAuthorities().stream()
                .anyMatch(result -> result.getAuthority().equals(role.getRole())
        );
        if (!hasRole)
            throw new CustomException(ErrorCode.ONLY_ADMIN_EXCEPTION);
    }

    private boolean checkAnnotation(Object handler, Class A) {
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        return handlerMethod.getMethodAnnotation(A) != null;
    }
}
