package com.kakao.together.event;

public class MemberSignupCompleteEvent {
    private final String key;

    public MemberSignupCompleteEvent(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
}
