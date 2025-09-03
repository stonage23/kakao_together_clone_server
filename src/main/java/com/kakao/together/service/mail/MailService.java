package com.kakao.together.service.mail;

public interface MailService {
    String sendSignupMail(String recipient);

    String sendPasswordResetMail(String recipient);
}
