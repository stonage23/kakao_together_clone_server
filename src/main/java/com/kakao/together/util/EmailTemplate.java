package com.kakao.together.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailTemplate {

    private EmailTemplate() {}

    public static String getSignupTemplate(String url) {
        StringBuilder template = new StringBuilder();
        template.append("<div style='margin:20px;'>")
                .append("<h1> 지금 이메일 인증을 마치고 KakaoTogether을 시작하세요! </h1>")
                .append("<br>")
                .append("<p> 이메일 인증 전에는 주요 기능을 이용하실 수 없습니다.<p>")
                .append("<br>")
                .append("<p>인증을 완료하시고 서비스를 이용해주세요. 본 이메일 인증은 30분간 유효합니다.<p>")
                .append("<br>")
                .append("<div align='center' style='border:1px solid black; font-family:verdana';>")
                .append("<h3 style='color:blue;'>아래 링크를 클릭하여 회원가입을 완료해주세요.</h3>")
                .append("<a href='" + url + "'>" + "클릭하여 계정을 활성화해주세요" + "</a>")
                .append("</div>")
        ;

        return template.toString();
    }

    public static String getPasswordResetTemplate(String url) {
        StringBuilder template = new StringBuilder();
        return template.toString();
    }
}
