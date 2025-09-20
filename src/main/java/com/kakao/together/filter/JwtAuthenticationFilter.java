package com.kakao.together.filter;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.token.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final String PARAM_USERNAME = "username";

    @Value("${jwt.access.header}")
    private String accessHeader;


    /**
     * Authentication 객체가 SecurityContext에 담기지 않았으면(AccessToken이 유효하지 않은 경우) 401에러 발생
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isPresentAccessToken(request)) {
            try {
                Authentication authentication = checkAccessTokenAndAuthentication(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (CustomException e) {
                log.info(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPresentAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).isPresent();
    }

    private Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .map(jwtService::removeBearerPrefix);
    }

    private Authentication checkAccessTokenAndAuthentication(HttpServletRequest request) {
        String accessToken = extractAccessToken(request).orElseGet(() -> "");
        Claims claims = jwtService.extractAllClaims(accessToken);

        String subject = claims.getSubject();
        if (subject.isEmpty()) throw new CustomException(ErrorCode.INVALID_TOKEN_PAYLOAD,"accesstoken에 유저 식별 정보 subject 없음");

        List<Map<String, String>> authClaim = claims.get("auth", List.class);
        Collection<GrantedAuthority> authorities = authClaim.stream()
                .map(authMap -> new SimpleGrantedAuthority(authMap.get("authority")))
                .collect(Collectors.toList());

        UserDetails principal = new CustomUserDetails(subject, authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
