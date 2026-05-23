package com.zzmg.topic_channel_platform.model;

public class ForgotPasswordRequest {
    private String phoneNumber;
    private String email;
    private String verificationCode;
    private String newPassword;
    private String confirmPassword;

    public ForgotPasswordRequest(String phoneNumber, String email, String verificationCode,
                                  String newPassword, String confirmPassword) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.verificationCode = verificationCode;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getVerificationCode() { return verificationCode; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
}
