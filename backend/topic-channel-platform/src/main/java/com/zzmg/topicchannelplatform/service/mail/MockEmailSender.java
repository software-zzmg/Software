package com.zzmg.topicchannelplatform.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mail.mock", havingValue = "true", matchIfMissing = true)
public class MockEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(MockEmailSender.class);

    @Override
    public boolean sendVerificationCode(String email, String code, String scene) {
        log.info("验证码邮件: email={}, scene={}, code={}", email, scene, code);
        return true;
    }
}
