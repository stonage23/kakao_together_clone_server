package com.kakao.together.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            log.info(e.getMessage());
            setErrorResponse(e, response);
//        } catch (RuntimeException e) {
//            log.warn("Filter 영역에서 핸들링되지 않은 예외 발생; 발생 예외: {}" + e.getMessage(), e);
//            CustomException customException = new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
//            setErrorResponse(customException, response);
        }
    }

    private void setErrorResponse(CustomException e, HttpServletResponse response) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        } catch (IOException ex) {
            log.warn("클라이언트로 에러 응답을 보내는 중 IO 예외 발생: {}; 원래 예외: {}", e.getMessage(), e.getMessage());
        }
    }
}
