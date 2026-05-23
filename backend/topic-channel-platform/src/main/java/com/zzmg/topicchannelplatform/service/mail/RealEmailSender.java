package com.zzmg.topicchannelplatform.service.mail;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mail.mock", havingValue = "false")
public class RealEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(RealEmailSender.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public RealEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean sendVerificationCode(String email, String code, String scene) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
            }
            helper.setTo(email);
            String subject = "register".equals(scene) ? "注册验证码" : "找回密码验证码";
            helper.setSubject(subject);
            helper.setText("<h3>您的验证码</h3><p style='font-size:24px;font-weight:bold;'>" + code
                    + "</p><p>5 分钟内有效，请勿泄露。</p>", true);
            mailSender.send(message);
            log.info("邮件已发送: email={}, scene={}", email, scene);
            return true;
        } catch (MailException | jakarta.mail.MessagingException e) {
            log.error("邮件发送失败: email={}, scene={}", email, scene, e);
            return false;
        }
    }
}
