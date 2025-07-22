package com.kakao.together.interceptor;

import com.kakao.together.annotation.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Profile("test")
public class CheckRoleTestController {

    @Admin
    @GetMapping("/admin/role/test")
    public String adminOnly() {
        log.info("##### Admin Only 요청 메소드 진입 성공!");
        return "테스트통과";
    }
}
