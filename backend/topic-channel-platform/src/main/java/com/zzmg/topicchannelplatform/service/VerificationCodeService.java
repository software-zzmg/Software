package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.service.mail.EmailSender;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class VerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeService.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int EXPIRE_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int SEND_INTERVAL_SECONDS = 60;

    private final EmailSender emailSender;

    public VerificationCodeService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public String sendCode(String scope, String email, HttpSession session) {
        String lastSentKey = scope + "LastSentAt";
        LocalDateTime lastSent = (LocalDateTime) session.getAttribute(lastSentKey);
        if (lastSent != null && lastSent.plusSeconds(SEND_INTERVAL_SECONDS).isAfter(
                LocalDateTime.now())) {
            long remain = SEND_INTERVAL_SECONDS - java.time.Duration.between(
                    lastSent, LocalDateTime.now()).getSeconds();
            return "请 " + remain + " 秒后重试";
        }

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        log.info("验证码: scope={}, email={}, code={}", scope, email, code);

        if (!emailSender.sendVerificationCode(email, code, scope)) {
            return "邮件发送失败，请稍后重试";
        }

        session.setAttribute(lastSentKey, LocalDateTime.now());
        session.setAttribute(scope + "Email", email);
        session.setAttribute(scope + "VerificationCode", code);
        session.setAttribute(scope + "CodeExpireAt", LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        session.setAttribute(scope + "CodeAttempts", 0);
        return null;
    }

    public String validate(String scope, String email, String code, HttpSession session) {
        String storedEmail = (String) session.getAttribute(scope + "Email");
        String storedCode = (String) session.getAttribute(scope + "VerificationCode");
        LocalDateTime expireAt = (LocalDateTime) session.getAttribute(scope + "CodeExpireAt");
        Integer attempts = (Integer) session.getAttribute(scope + "CodeAttempts");

        if (storedCode == null || !email.equals(storedEmail)) {
            return "请先获取验证码";
        }
        if (expireAt != null && LocalDateTime.now().isAfter(expireAt)) {
            clear(scope, session);
            return "验证码已过期，请重新获取";
        }
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            clear(scope, session);
            return "验证次数过多，请重新获取验证码";
        }
        if (!storedCode.equals(code)) {
            session.setAttribute(scope + "CodeAttempts", attempts != null ? attempts + 1 : 1);
            return "验证码错误";
        }
        clear(scope, session);
        return null;
    }

    public void clear(String scope, HttpSession session) {
        session.removeAttribute(scope + "Email");
        session.removeAttribute(scope + "VerificationCode");
        session.removeAttribute(scope + "CodeExpireAt");
        session.removeAttribute(scope + "CodeAttempts");
    }
}
