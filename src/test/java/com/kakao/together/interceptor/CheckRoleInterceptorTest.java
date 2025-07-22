package com.kakao.together.interceptor;

import com.kakao.together.TestUtil;
import com.kakao.together.config.NoSecurityConfig;
import com.kakao.together.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(CheckRoleInterceptorTest.class)
@Import({NoSecurityConfig.class, CheckRoleTestController.class})
@ActiveProfiles("test")
@Slf4j
class CheckRoleInterceptorTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("권한이 아예 없는 계정이 only 관리자 요청에 접근")
    void noAdminRequest() throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/admin/role/test")
                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        log.info("#####" + result.getResponse().getContentAsString());

        assertThat(result.getResponse().getStatus()).isEqualTo(ErrorCode.ONLY_ADMIN_EXCEPTION.getHttpStatus().value());
        assertThat((String) TestUtil.fromJson(result, "message")).isEqualTo(ErrorCode.ONLY_ADMIN_EXCEPTION.getMessage());
        assertThat((Double) TestUtil.fromJson(result, "status")).isEqualTo(403);
    }

    @Test
    @DisplayName("관리자가 아닌 다른 권한을 가진 계정이 Only 관리자 요청에 접근")
    @WithMockUser(username = "member", authorities = "ROLE_MEMBER")
    void requestWithInvalidAuthority() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/admin/role/test")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("관리자가 Only관리자 요청에 접근")
    @WithMockUser(username = "admin", authorities = "ROLE_ADMIN")
    void adminRequest() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/admin/role/test")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("테스트통과");
    }
}