package com.kakao.together.controller.fundraising;

import com.google.gson.Gson;
import com.kakao.together.controller.auth.AuthController;
import com.kakao.together.controller.auth.dto.AuthDto.SignupByEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
//@Import({NoSecurityConfig.class})
@Slf4j
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @Autowired
    private AuthController authController;


    @Test
    void signupRequest() throws Exception {
        SignupByEmailRequest request = new SignupByEmailRequest("test@email.com", "testPassword", "testNickname", "20", "테스트 주소");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(request))
        ).andDo(print());


//        log.info("#####" + response.getResponse().getContentAsString());
    }
}
