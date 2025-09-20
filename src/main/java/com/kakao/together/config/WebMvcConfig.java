package com.kakao.together.config;

import com.kakao.together.interceptor.CheckRoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CheckRoleInterceptor checkRoleInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // 1. 웹에서 접근할 URL 경로 (예: http://localhost:8080/images/...)
                // 2. 실제 파일이 저장된 로컬 경로. 반드시 "file:///"로 시작해야 합니다.
                .addResourceLocations("file:///C:/Users/Stonage/Desktop/kakao_together_clone/server/kakao_together_clone/src/main/resources/imgs/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // "/**"는 모든 경로에 대해 CORS 설정을 적용한다는 의미
                .allowedOrigins("http://localhost:3000") // 허용할 출처(클라이언트 주소)를 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 허용할 HTTP 헤더
                .allowCredentials(true) // 쿠키 등 자격 증명을 허용
                .maxAge(3600); // pre-flight 요청의 캐시 시간(초)
    }


//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(checkRoleInterceptor)
//                .addPathPatterns("/api/members/**")
//                .addPathPatterns("/admin/**");
//    }
}
