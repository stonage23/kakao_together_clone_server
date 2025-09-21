package com.kakao.together.external.mail;

import com.kakao.together.service.mail.MailService;
import com.kakao.together.util.CodeGeneratorUtil;
import com.kakao.together.util.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpMailService implements MailService {


    private final JavaMailSender javaMailSender;

    private String sender = "카카오투게더";
    @Value("${amdin.config.signup.url}")
    private String signupUrl;
    @Value("${spring.mail.charset}")
    private String charset;
    @Value("${spring.mail.subtype}")
    private String subtype;
    @Value("${spring.mail.host}")
    private String host;


    @Override
    public String sendSignupMail(String recipient) {

        String code = getSignupCode();
        String url = signupUrl + "?code=" + code;

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setSubject("인증하시고 서비스를 이용해주세요!");
            message.addRecipients(MimeMessage.RecipientType.TO, recipient);
            message.setText(EmailTemplate.getSignupTemplate(url), charset, subtype);
            message.setFrom(new InternetAddress(host, sender));

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error building email message", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while creating InternetAddress", e);
        } catch (MailException e) {
            log.error("Error sending email", e);
        }

        log.debug("생성된 회원가입 인증 코드: " + code);

        return code;
    }

    @Override
    public String sendPasswordResetMail(String recipient) {
        return null;
    }

    private String getSignupCode() {
        return CodeGeneratorUtil.createRandomCode(8);
    }
}
