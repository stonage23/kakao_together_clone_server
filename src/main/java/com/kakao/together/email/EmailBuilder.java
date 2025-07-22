package com.kakao.together.email;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailBuilder {

    private String sender;
    private String recipient;
    private String subject;
    private String text;
}
