package com.kakao.together.api.email;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Profile("mail")
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.charset}")
    private String charset;
    @Value("${spring.mail.subtype}")
    private String subtype;
    @Value("${spring.mail.host}")
    private String host;

    public boolean sendMail(String sender, String recipient, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.addRecipients(Message.RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setText(text, charset, subtype);
            message.setFrom(new InternetAddress(host, sender));
            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Error building email message", e);
            return false;
        } catch (UnsupportedEncodingException e) {
            log.error("Error while creating InternetAddress", e);
            return false;
        } catch (MailException e) {
            log.error("Error sending email", e);
            return false;
        }

        return true;
    }
    public void sendMail(EmailBuilder builder) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.addRecipients(Message.RecipientType.TO, builder.getRecipient());
            message.setSubject(builder.getSubject());
            message.setText(builder.getText(), charset, subtype);
            message.setFrom(new InternetAddress(host, builder.getSender()));
            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Error building email message: {}; {}; {};", e, builder.getSender(), builder.getRecipient());
            throw new CustomException(ErrorCode.INTERNAL_MAIL_ERROR);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while creating InternetAddress: {}; {}; {};", e, builder.getSender(), builder.getRecipient());
            throw new CustomException(ErrorCode.INTERNAL_MAIL_ERROR);
        } catch (MailException e) {
            log.error("Error sending email: {}; {}; {}", e, builder.getSender(), builder.getRecipient());
            throw new CustomException(ErrorCode.INTERNAL_MAIL_ERROR);
        }
    }
}