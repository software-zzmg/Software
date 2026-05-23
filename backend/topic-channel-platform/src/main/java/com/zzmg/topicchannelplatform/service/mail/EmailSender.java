package com.zzmg.topicchannelplatform.service.mail;

public interface EmailSender {
    boolean sendVerificationCode(String email, String code, String scene);
}
